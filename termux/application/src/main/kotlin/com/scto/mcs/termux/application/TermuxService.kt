package com.scto.mcs.termux.application

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import com.termux.R
import com.scto.mcs.termux.application.event.SystemEventReceiver
import com.scto.mcs.termux.application.terminal.TermuxTerminalSessionActivityClient
import com.scto.mcs.termux.application.terminal.TermuxTerminalSessionServiceClient
import com.scto.mcs.termux.shared.android.PermissionUtils
import com.scto.mcs.termux.shared.data.DataUtils
import com.scto.mcs.termux.shared.data.IntentUtils
import com.scto.mcs.termux.shared.errors.Errno
import com.scto.mcs.termux.shared.logger.Logger
import com.scto.mcs.termux.shared.net.uri.UriUtils
import com.scto.mcs.termux.shared.notification.NotificationUtils
import com.scto.mcs.termux.shared.shell.ShellUtils
import com.scto.mcs.termux.shared.shell.command.ExecutionCommand
import com.scto.mcs.termux.shared.shell.command.runner.app.AppShell
import com.scto.mcs.termux.shared.termux.TermuxConstants
import com.scto.mcs.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY
import com.scto.mcs.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_SERVICE
import com.scto.mcs.termux.shared.termux.plugins.TermuxPluginUtils
import com.scto.mcs.termux.shared.termux.settings.properties.TermuxAppSharedProperties
import com.scto.mcs.termux.shared.termux.shell.TermuxShellManager
import com.scto.mcs.termux.shared.termux.shell.TermuxShellUtils
import com.scto.mcs.termux.shared.termux.shell.command.environment.TermuxShellEnvironment
import com.scto.mcs.termux.shared.termux.shell.command.runner.terminal.TermuxSession
import com.scto.mcs.termux.shared.termux.terminal.TermuxTerminalSessionClientBase
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import java.io.Closeable
import java.util.ArrayList

class TermuxService : Service(), AppShell.AppShellClient, TermuxSession.TermuxSessionClient {

    inner class LocalBinder : Binder(), Closeable {
        val service: TermuxService = this@TermuxService
        override fun close() {
            // No-op
        }
    }

    private val mBinder = LocalBinder()
    private val mHandler = Handler(Looper.getMainLooper())

    private var mTermuxTerminalSessionActivityClient: TermuxTerminalSessionActivityClient? = null
    private val mTermuxTerminalSessionServiceClient = TermuxTerminalSessionServiceClient(this)

    private var mProperties: TermuxAppSharedProperties? = null
    private var mShellManager: TermuxShellManager? = null

    private var mWakeLock: PowerManager.WakeLock? = null
    private var mWifiLock: WifiManager.WifiLock? = null

    var wantsToStop: Boolean = false
        private set

    companion object {
        private const val LOG_TAG = "TermuxService"
    }

    override fun onCreate() {
        Logger.logVerbose(LOG_TAG, "onCreate")
        mProperties = TermuxAppSharedProperties.getProperties()
        mShellManager = TermuxShellManager.getShellManager()
        runStartForeground()
        SystemEventReceiver.registerPackageUpdateEvents(this)
    }

    @SuppressLint("Wakelock")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.logDebug(LOG_TAG, "onStartCommand")
        runStartForeground()

        val action = intent?.action
        if (action != null) {
            Logger.logVerboseExtended(LOG_TAG, "Intent Received:\n" + IntentUtils.getIntentString(intent))
            when (action) {
                TERMUX_SERVICE.ACTION_STOP_SERVICE -> actionStopService()
                TERMUX_SERVICE.ACTION_WAKE_LOCK -> actionAcquireWakeLock()
                TERMUX_SERVICE.ACTION_WAKE_UNLOCK -> actionReleaseWakeLock(true)
                TERMUX_SERVICE.ACTION_SERVICE_EXECUTE -> actionServiceExecute(intent)
                else -> Logger.logError(LOG_TAG, "Invalid action: \"$action\"")
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Logger.logVerbose(LOG_TAG, "onDestroy")
        TermuxShellUtils.clearTermuxTMPDIR(true)
        actionReleaseWakeLock(false)
        if (!wantsToStop) killAllTermuxExecutionCommands()
        TermuxShellManager.onAppExit(this)
        SystemEventReceiver.unregisterPackageUpdateEvents(this)
        runStopForeground()
    }

    override fun onBind(intent: Intent?): IBinder = mBinder

    override fun onUnbind(intent: Intent?): Boolean {
        Logger.logVerbose(LOG_TAG, "onUnbind")
        if (mTermuxTerminalSessionActivityClient != null) unsetTermuxTerminalSessionClient()
        return false
    }

    private fun runStartForeground() {
        setupNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(TermuxConstants.TERMUX_APP_NOTIFICATION_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(TermuxConstants.TERMUX_APP_NOTIFICATION_ID, buildNotification())
        }
    }

    private fun runStopForeground() {
        stopForeground(true)
    }

    private fun requestStopService() {
        Logger.logDebug(LOG_TAG, "Requesting to stop service")
        runStopForeground()
        mTermuxTerminalSessionServiceClient.close()
        stopSelf()
    }

    private fun actionStopService() {
        wantsToStop = true
        killAllTermuxExecutionCommands()
        requestStopService()
    }

    @Synchronized
    private fun killAllTermuxExecutionCommands() {
        val shellManager = mShellManager ?: return
        Logger.logDebug(LOG_TAG, "Killing TermuxSessions=${shellManager.mTermuxSessions.size}, TermuxTasks=${shellManager.mTermuxTasks.size}, PendingPluginExecutionCommands=${shellManager.mPendingPluginExecutionCommands.size}")

        val termuxSessions = ArrayList(shellManager.mTermuxSessions)
        val termuxTasks = ArrayList(shellManager.mTermuxTasks)
        val pendingPluginExecutionCommands = ArrayList(shellManager.mPendingPluginExecutionCommands)

        for (session in termuxSessions) {
            val executionCommand = session.executionCommand
            val processResult = wantsToStop || executionCommand.isPluginExecutionCommandWithPendingResult
            session.killIfExecuting(this, processResult)
            if (!processResult) shellManager.mTermuxSessions.remove(session)
        }

        for (task in termuxTasks) {
            if (task.executionCommand.isPluginExecutionCommandWithPendingResult) task.killIfExecuting(this, true)
            else shellManager.mTermuxTasks.remove(task)
        }

        for (executionCommand in pendingPluginExecutionCommands) {
            if (!executionCommand.shouldNotProcessResults() && executionCommand.isPluginExecutionCommandWithPendingResult) {
                if (executionCommand.setStateFailed(Errno.ERRNO_CANCELLED.code, this.getString(com.termux.shared.R.string.error_execution_cancelled))) {
                    TermuxPluginUtils.processPluginExecutionCommandResult(this, LOG_TAG, executionCommand)
                }
            }
        }
    }

    @SuppressLint("WakelockTimeout", "BatteryLife")
    private fun actionAcquireWakeLock() {
        if (mWakeLock != null) return
        Logger.logDebug(LOG_TAG, "Acquiring WakeLocks")
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TermuxConstants.TERMUX_APP_NAME.lowercase() + ":service-wakelock")
        mWakeLock?.acquire()
        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mWifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, TermuxConstants.TERMUX_APP_NAME.lowercase())
        mWifiLock?.acquire()
        if (!PermissionUtils.checkIfBatteryOptimizationsDisabled(this)) PermissionUtils.requestDisableBatteryOptimizations(this)
        updateNotification()
    }

    private fun actionReleaseWakeLock(updateNotification: Boolean) {
        if (mWakeLock == null && mWifiLock == null) return
        Logger.logDebug(LOG_TAG, "Releasing WakeLocks")
        mWakeLock?.release()
        mWakeLock = null
        mWifiLock?.release()
        mWifiLock = null
        if (updateNotification) updateNotification()
    }

    private fun actionServiceExecute(intent: Intent) {
        val executionCommand = ExecutionCommand(TermuxShellManager.getNextShellId())
        executionCommand.executableUri = intent.data
        executionCommand.isPluginExecutionCommand = true
        executionCommand.runner = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RUNNER,
            if (intent.getBooleanExtra(TERMUX_SERVICE.EXTRA_BACKGROUND, false)) ExecutionCommand.Runner.APP_SHELL.runnerName else ExecutionCommand.Runner.TERMINAL_SESSION.runnerName)
        
        if (ExecutionCommand.Runner.runnerOf(executionCommand.runner) == null) {
            val errmsg = this.getString(R.string.error_termux_service_invalid_execution_command_runner, executionCommand.runner)
            executionCommand.setStateFailed(Errno.ERRNO_FAILED.code, errmsg)
            TermuxPluginUtils.processPluginExecutionCommandError(this, LOG_TAG, executionCommand, false)
            return
        }

        if (executionCommand.executableUri != null) {
            executionCommand.executable = UriUtils.getUriFilePathWithFragment(executionCommand.executableUri)
            executionCommand.arguments = IntentUtils.getStringArrayExtraIfSet(intent, TERMUX_SERVICE.EXTRA_ARGUMENTS, null)
            if (ExecutionCommand.Runner.APP_SHELL.equalsRunner(executionCommand.runner))
                executionCommand.stdin = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_STDIN, null)
            executionCommand.backgroundCustomLogLevel = IntentUtils.getIntegerExtraIfSet(intent, TERMUX_SERVICE.EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL, null)
        }

        executionCommand.workingDirectory = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_WORKDIR, null)
        executionCommand.isFailsafe = intent.getBooleanExtra(TERMUX_ACTIVITY.EXTRA_FAILSAFE_SESSION, false)
        executionCommand.sessionAction = intent.getStringExtra(TERMUX_SERVICE.EXTRA_SESSION_ACTION)
        executionCommand.shellName = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_SHELL_NAME, null)
        executionCommand.shellCreateMode = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_SHELL_CREATE_MODE, null)
        executionCommand.commandLabel = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_COMMAND_LABEL, "Execution Intent Command")
        executionCommand.commandDescription = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_COMMAND_DESCRIPTION, null)
        executionCommand.commandHelp = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_COMMAND_HELP, null)
        executionCommand.pluginAPIHelp = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_PLUGIN_API_HELP, null)
        executionCommand.resultConfig.resultPendingIntent = intent.getParcelableExtra(TERMUX_SERVICE.EXTRA_PENDING_INTENT)
        executionCommand.resultConfig.resultDirectoryPath = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_DIRECTORY, null)
        if (executionCommand.resultConfig.resultDirectoryPath != null) {
            executionCommand.resultConfig.resultSingleFile = intent.getBooleanExtra(TERMUX_SERVICE.EXTRA_RESULT_SINGLE_FILE, false)
            executionCommand.resultConfig.resultFileBasename = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_FILE_BASENAME, null)
            executionCommand.resultConfig.resultFileOutputFormat = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_FILE_OUTPUT_FORMAT, null)
            executionCommand.resultConfig.resultFileErrorFormat = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_FILE_ERROR_FORMAT, null)
            executionCommand.resultConfig.resultFilesSuffix = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_FILES_SUFFIX, null)
        }

        if (executionCommand.shellCreateMode == null) executionCommand.shellCreateMode = ExecutionCommand.ShellCreateMode.ALWAYS.mode

        mShellManager?.mPendingPluginExecutionCommands?.add(executionCommand)

        if (ExecutionCommand.Runner.APP_SHELL.equalsRunner(executionCommand.runner)) executeTermuxTaskCommand(executionCommand)
        else if (ExecutionCommand.Runner.TERMINAL_SESSION.equalsRunner(executionCommand.runner)) executeTermuxSessionCommand(executionCommand)
        else {
            val errmsg = getString(R.string.error_termux_service_unsupported_execution_command_runner, executionCommand.runner)
            executionCommand.setStateFailed(Errno.ERRNO_FAILED.code, errmsg)
            TermuxPluginUtils.processPluginExecutionCommandError(this, LOG_TAG, executionCommand, false)
        }
    }

    private fun executeTermuxTaskCommand(executionCommand: ExecutionCommand) {
        if (executionCommand.shellName == null && executionCommand.executable != null)
            executionCommand.shellName = ShellUtils.getExecutableBasename(executionCommand.executable)

        var newTermuxTask: AppShell? = null
        val shellCreateMode = processShellCreateMode(executionCommand) ?: return
        if (ExecutionCommand.ShellCreateMode.NO_SHELL_WITH_NAME.equalsMode(shellCreateMode)) {
            newTermuxTask = getTermuxTaskForShellName(executionCommand.shellName)
        }

        if (newTermuxTask == null) createTermuxTask(executionCommand)
    }

    fun createTermuxTask(executablePath: String?, arguments: Array<String>?, stdin: String?, workingDirectory: String?): AppShell? {
        return createTermuxTask(ExecutionCommand(TermuxShellManager.getNextShellId(), executablePath, arguments, stdin, workingDirectory, ExecutionCommand.Runner.APP_SHELL.runnerName, false))
    }

    @Synchronized
    fun createTermuxTask(executionCommand: ExecutionCommand): AppShell? {
        if (!ExecutionCommand.Runner.APP_SHELL.equalsRunner(executionCommand.runner)) return null
        executionCommand.setShellCommandShellEnvironment = true
        val newTermuxTask = AppShell.execute(this, executionCommand, this, TermuxShellEnvironment(), null, false)
        if (newTermuxTask == null) {
            if (executionCommand.isPluginExecutionCommand) TermuxPluginUtils.processPluginExecutionCommandError(this, LOG_TAG, executionCommand, false)
            return null
        }
        mShellManager?.mTermuxTasks?.add(newTermuxTask)
        if (executionCommand.isPluginExecutionCommand) mShellManager?.mPendingPluginExecutionCommands?.remove(executionCommand)
        updateNotification()
        return newTermuxTask
    }

    override fun onAppShellExited(termuxTask: AppShell?) {
        mHandler.post {
            if (termuxTask != null) {
                val executionCommand = termuxTask.executionCommand
                if (executionCommand.isPluginExecutionCommand) TermuxPluginUtils.processPluginExecutionCommandResult(this, LOG_TAG, executionCommand)
                mShellManager?.mTermuxTasks?.remove(termuxTask)
            }
            updateNotification()
        }
    }

    private fun executeTermuxSessionCommand(executionCommand: ExecutionCommand) {
        if (executionCommand.shellName == null && executionCommand.executable != null)
            executionCommand.shellName = ShellUtils.getExecutableBasename(executionCommand.executable)

        var newTermuxSession: TermuxSession? = null
        val shellCreateMode = processShellCreateMode(executionCommand) ?: return
        if (ExecutionCommand.ShellCreateMode.NO_SHELL_WITH_NAME.equalsMode(shellCreateMode)) {
            newTermuxSession = getTermuxSessionForShellName(executionCommand.shellName)
        }

        if (newTermuxSession == null) newTermuxSession = createTermuxSession(executionCommand)
        if (newTermuxSession == null) return

        handleSessionAction(DataUtils.getIntFromString(executionCommand.sessionAction, TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY), newTermuxSession.terminalSession)
    }

    fun createTermuxSession(executablePath: String?, arguments: Array<String>?, stdin: String?, workingDirectory: String?, isFailSafe: Boolean, sessionName: String?): TermuxSession? {
        val executionCommand = ExecutionCommand(TermuxShellManager.getNextShellId(), executablePath, arguments, stdin, workingDirectory, ExecutionCommand.Runner.TERMINAL_SESSION.runnerName, isFailSafe)
        executionCommand.shellName = sessionName
        return createTermuxSession(executionCommand)
    }

    @Synchronized
    fun createTermuxSession(executionCommand: ExecutionCommand): TermuxSession? {
        if (!ExecutionCommand.Runner.TERMINAL_SESSION.equalsRunner(executionCommand.runner)) return null
        executionCommand.setShellCommandShellEnvironment = true
        executionCommand.terminalTranscriptRows = mProperties?.getTerminalTranscriptRows() ?: 2000

        val newTermuxSession = TermuxSession.execute(this, executionCommand, getTermuxTerminalSessionClient(), this, TermuxShellEnvironment(), null, executionCommand.isPluginExecutionCommand)
        if (newTermuxSession == null) {
            if (executionCommand.isPluginExecutionCommand) TermuxPluginUtils.processPluginExecutionCommandError(this, LOG_TAG, executionCommand, false)
            return null
        }
        mShellManager?.mTermuxSessions?.add(newTermuxSession)
        if (executionCommand.isPluginExecutionCommand) mShellManager?.mPendingPluginExecutionCommands?.remove(executionCommand)
        mTermuxTerminalSessionActivityClient?.termuxSessionListNotifyUpdated()
        updateNotification()
        TermuxActivity.updateTermuxActivityStyling(this, false)
        return newTermuxSession
    }

    @Synchronized
    fun removeTermuxSession(sessionToRemove: TerminalSession): Int {
        val index = getIndexOfSession(sessionToRemove)
        if (index >= 0) mShellManager?.mTermuxSessions?.get(index)?.finish()
        return index
    }

    override fun onTermuxSessionExited(termuxSession: TermuxSession?) {
        if (termuxSession != null) {
            val executionCommand = termuxSession.executionCommand
            if (executionCommand.isPluginExecutionCommand) TermuxPluginUtils.processPluginExecutionCommandResult(this, LOG_TAG, executionCommand)
            mShellManager?.mTermuxSessions?.remove(termuxSession)
            mTermuxTerminalSessionActivityClient?.termuxSessionListNotifyUpdated()
        }
        updateNotification()
    }

    private fun processShellCreateMode(executionCommand: ExecutionCommand): ExecutionCommand.ShellCreateMode? {
        val mode = executionCommand.shellCreateMode
        if (ExecutionCommand.ShellCreateMode.ALWAYS.equalsMode(mode)) return ExecutionCommand.ShellCreateMode.ALWAYS
        if (ExecutionCommand.ShellCreateMode.NO_SHELL_WITH_NAME.equalsMode(mode)) {
            if (DataUtils.isNullOrEmpty(executionCommand.shellName)) {
                TermuxPluginUtils.setAndProcessPluginExecutionCommandError(this, LOG_TAG, executionCommand, false, getString(R.string.error_termux_service_execution_command_shell_name_unset, mode))
                return null
            }
            return ExecutionCommand.ShellCreateMode.NO_SHELL_WITH_NAME
        }
        TermuxPluginUtils.setAndProcessPluginExecutionCommandError(this, LOG_TAG, executionCommand, false, getString(R.string.error_termux_service_unsupported_execution_command_shell_create_mode, mode))
        return null
    }

    private fun handleSessionAction(sessionAction: Int, newTerminalSession: TerminalSession) {
        when (sessionAction) {
            TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY -> {
                setCurrentStoredTerminalSession(newTerminalSession)
                mTermuxTerminalSessionActivityClient?.setCurrentSession(newTerminalSession)
                startTermuxActivity()
            }
            TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY -> {
                if (getTermuxSessionsSize() == 1) setCurrentStoredTerminalSession(newTerminalSession)
                startTermuxActivity()
            }
            TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_DONT_OPEN_ACTIVITY -> {
                setCurrentStoredTerminalSession(newTerminalSession)
                mTermuxTerminalSessionActivityClient?.setCurrentSession(newTerminalSession)
            }
            TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_DONT_OPEN_ACTIVITY -> {
                if (getTermuxSessionsSize() == 1) setCurrentStoredTerminalSession(newTerminalSession)
            }
            else -> {
                Logger.logError(LOG_TAG, "Invalid sessionAction: \"$sessionAction\". Force using default sessionAction.")
                handleSessionAction(TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY, newTerminalSession)
            }
        }
    }

    private fun startTermuxActivity() {
        if (PermissionUtils.validateDisplayOverOtherAppsPermissionForPostAndroid10(this, true)) {
            TermuxActivity.startTermuxActivity(this)
        } else {
            val preferences = TermuxAppSharedPreferences.build(this) ?: return
            if (preferences.arePluginErrorNotificationsEnabled(false))
                Logger.showToast(this, this.getString(R.string.error_display_over_other_apps_permission_not_granted_to_start_terminal), true)
        }
    }

    @Synchronized
    fun getTermuxTerminalSessionClient(): TermuxTerminalSessionClientBase {
        return mTermuxTerminalSessionActivityClient ?: mTermuxTerminalSessionServiceClient
    }

    @Synchronized
    fun setTermuxTerminalSessionClient(termuxTerminalSessionActivityClient: TermuxTerminalSessionActivityClient) {
        mTermuxTerminalSessionActivityClient = termuxTerminalSessionActivityClient
        for (session in mShellManager!!.mTermuxSessions)
            session.terminalSession.updateTerminalSessionClient(mTermuxTerminalSessionActivityClient)
    }

    @Synchronized
    fun unsetTermuxTerminalSessionClient() {
        for (session in mShellManager!!.mTermuxSessions)
            session.terminalSession.updateTerminalSessionClient(mTermuxTerminalSessionServiceClient)
        mTermuxTerminalSessionActivityClient = null
    }

    private fun buildNotification(): Notification? {
        val res = resources
        val notificationIntent = TermuxActivity.newInstance(this)
        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        
        val sessionCount = getTermuxSessionsSize()
        val taskCount = mShellManager?.mTermuxTasks?.size ?: 0
        var notificationText = "$sessionCount session" + (if (sessionCount == 1) "" else "s")
        if (taskCount > 0) notificationText += ", $taskCount task" + (if (taskCount == 1) "" else "s")
        val wakeLockHeld = mWakeLock != null
        if (wakeLockHeld) notificationText += " (wake lock held)"

        val priority = if (wakeLockHeld) Notification.PRIORITY_HIGH else Notification.PRIORITY_LOW
        val builder = NotificationUtils.geNotificationBuilder(this, TermuxConstants.TERMUX_APP_NOTIFICATION_CHANNEL_ID, priority, TermuxConstants.TERMUX_APP_NAME, notificationText, null, contentIntent, null, NotificationUtils.NOTIFICATION_MODE_SILENT) ?: return null

        builder.setShowWhen(false)
        builder.setSmallIcon(R.drawable.ic_service_notification)
        builder.setColor(0xFF607D8B.toInt())
        builder.setOngoing(true)

        val exitIntent = Intent(this, TermuxService::class.java).setAction(TERMUX_SERVICE.ACTION_STOP_SERVICE)
        builder.addAction(android.R.drawable.ic_delete, res.getString(R.string.notification_action_exit), PendingIntent.getService(this, 0, exitIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0))

        val newWakeAction = if (wakeLockHeld) TERMUX_SERVICE.ACTION_WAKE_UNLOCK else TERMUX_SERVICE.ACTION_WAKE_LOCK
        val toggleWakeLockIntent = Intent(this, TermuxService::class.java).setAction(newWakeAction)
        val actionTitle = res.getString(if (wakeLockHeld) R.string.notification_action_wake_unlock else R.string.notification_action_wake_lock)
        val actionIcon = if (wakeLockHeld) android.R.drawable.ic_lock_idle_lock else android.R.drawable.ic_lock_lock
        builder.addAction(actionIcon, actionTitle, PendingIntent.getService(this, 0, toggleWakeLockIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0))

        return builder.build()
    }

    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        NotificationUtils.setupNotificationChannel(this, TermuxConstants.TERMUX_APP_NOTIFICATION_CHANNEL_ID, TermuxConstants.TERMUX_APP_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
    }

    @Synchronized
    fun updateNotification() {
        if (mWakeLock == null && (mShellManager?.mTermuxSessions?.isEmpty() == true) && (mShellManager?.mTermuxTasks?.isEmpty() == true)) {
            requestStopService()
        } else {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(TermuxConstants.TERMUX_APP_NOTIFICATION_ID, buildNotification())
        }
    }

    private fun setCurrentStoredTerminalSession(terminalSession: TerminalSession) {
        TermuxAppSharedPreferences.build(this)?.setCurrentSession(terminalSession.mHandle)
    }

    @Synchronized
    fun isTermuxSessionsEmpty(): Boolean = mShellManager?.mTermuxSessions?.isEmpty() == true

    @Synchronized
    fun getTermuxSessionsSize(): Int = mShellManager?.mTermuxSessions?.size ?: 0

    @Synchronized
    fun getTermuxSessions(): List<TermuxSession> = mShellManager?.mTermuxSessions ?: emptyList()

    @Synchronized
    fun getTermuxSession(index: Int): TermuxSession? = if (index >= 0 && index < (mShellManager?.mTermuxSessions?.size ?: 0)) mShellManager?.mTermuxSessions?.get(index) else null

    @Synchronized
    fun getTermuxSessionForTerminalSession(terminalSession: TerminalSession): TermuxSession? {
        return mShellManager?.mTermuxSessions?.find { it.terminalSession == terminalSession }
    }

    @Synchronized
    fun getLastTermuxSession(): TermuxSession? = mShellManager?.mTermuxSessions?.lastOrNull()

    @Synchronized
    fun getIndexOfSession(terminalSession: TerminalSession): Int = mShellManager?.mTermuxSessions?.indexOfFirst { it.terminalSession == terminalSession } ?: -1

    @Synchronized
    fun getTerminalSessionForHandle(sessionHandle: String): TerminalSession? = mShellManager?.mTermuxSessions?.find { it.terminalSession.mHandle == sessionHandle }?.terminalSession

    @Synchronized
    fun getTermuxTaskForShellName(name: String): AppShell? = if (DataUtils.isNullOrEmpty(name)) null else mShellManager?.mTermuxTasks?.find { it.executionCommand.shellName == name }

    @Synchronized
    fun getTermuxSessionForShellName(name: String): TermuxSession? = if (DataUtils.isNullOrEmpty(name)) null else mShellManager?.mTermuxSessions?.find { it.executionCommand.shellName == name }
}
