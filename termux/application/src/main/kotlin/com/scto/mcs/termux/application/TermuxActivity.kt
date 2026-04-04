package com.scto.mcs.termux.application

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.autofill.AutofillManager
import android.widget.EditText
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.scto.mcs.termux.application.activities.HelpActivity
import com.scto.mcs.termux.application.terminal.TermuxActivityRootView
import com.scto.mcs.termux.application.terminal.TermuxSessionsListViewController
import com.scto.mcs.termux.application.terminal.TermuxTerminalSessionActivityClient
import com.scto.mcs.termux.application.terminal.TermuxTerminalViewClient
import com.scto.mcs.termux.application.terminal.io.TerminalToolbarViewPager
import com.scto.mcs.termux.application.terminal.io.TermuxTerminalExtraKeys
import com.scto.mcs.termux.shared.activities.ReportActivity
import com.scto.mcs.termux.shared.activity.ActivityUtils
import com.scto.mcs.termux.shared.activity.media.AppCompatActivityUtils
import com.scto.mcs.termux.shared.android.PermissionUtils
import com.scto.mcs.termux.shared.data.DataUtils
import com.scto.mcs.termux.shared.data.IntentUtils
import com.scto.mcs.termux.shared.logger.Logger
import com.scto.mcs.termux.shared.termux.TermuxConstants
import com.scto.mcs.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY
import com.scto.mcs.termux.shared.termux.TermuxUtils
import com.scto.mcs.termux.shared.termux.crash.TermuxCrashUtils
import com.scto.mcs.termux.shared.termux.extrakeys.ExtraKeysView
import com.scto.mcs.termux.shared.termux.interact.TextInputDialogUtils
import com.scto.mcs.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences
import com.scto.mcs.termux.shared.termux.settings.properties.TermuxAppSharedProperties
import com.scto.mcs.termux.shared.termux.shell.command.runner.terminal.TermuxSession
import com.scto.mcs.termux.shared.termux.theme.TermuxThemeUtils
import com.scto.mcs.termux.shared.theme.NightMode
import com.scto.mcs.termux.shared.view.ViewUtils
import com.termux.R
import com.termux.terminal.TerminalSession
import com.termux.view.TerminalView
import com.tom.rv2ide.app.BaseIDEActivity
import java.util.Arrays
import java.util.Objects
import java.util.Optional

/**
 * A terminal emulator activity.
 */
open class TermuxActivity : BaseIDEActivity(), ServiceConnection {

    protected var mTermuxService: TermuxService? = null
    protected lateinit var mTerminalView: TerminalView
    protected var mTermuxTerminalViewClient: TermuxTerminalViewClient? = null
    protected var mTermuxTerminalSessionActivityClient: TermuxTerminalSessionActivityClient? = null
    protected var mPreferences: TermuxAppSharedPreferences? = null
    protected var mProperties: TermuxAppSharedProperties? = null
    protected lateinit var mTermuxActivityRootView: TermuxActivityRootView
    protected var mTermuxActivityBottomSpaceView: View? = null
    protected var mExtraKeysView: ExtraKeysView? = null
    protected var mTermuxTerminalExtraKeys: TermuxTerminalExtraKeys? = null
    protected var mTermuxSessionListViewController: TermuxSessionsListViewController? = null
    protected var mLastToast: Toast? = null
    protected var mIsVisible: Boolean = false
    protected var mIsOnResumeAfterOnCreate: Boolean = false
    protected var mIsActivityRecreated: Boolean = false
    protected var mIsInvalidState: Boolean = false
    protected var mNavBarHeight: Int = 0
    protected var mTerminalToolbarDefaultHeight: Float = 0f

    companion object {
        protected const val CONTEXT_MENU_SELECT_URL_ID = 0
        protected const val CONTEXT_MENU_SHARE_TRANSCRIPT_ID = 1
        protected const val CONTEXT_MENU_SHARE_SELECTED_TEXT = 10
        protected const val CONTEXT_MENU_AUTOFILL_ID = 2
        protected const val CONTEXT_MENU_RESET_TERMINAL_ID = 3
        protected const val CONTEXT_MENU_KILL_PROCESS_ID = 4
        protected const val CONTEXT_MENU_TOGGLE_KEEP_SCREEN_ON = 6
        protected const val CONTEXT_MENU_HELP_ID = 7
        protected const val CONTEXT_MENU_REPORT_ID = 9

        protected const val ARG_TERMINAL_TOOLBAR_TEXT_INPUT = "terminal_toolbar_text_input"
        protected const val ARG_ACTIVITY_RECREATED = "activity_recreated"
        protected const val LOG_TAG = "TermuxActivity"

        @JvmStatic
        fun startTermuxActivity(@NonNull context: Context) {
            ActivityUtils.startActivity(context, newInstance(context))
        }

        @JvmStatic
        fun newInstance(@NonNull context: Context): Intent {
            val intent = Intent(context, TermuxActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }

        @JvmStatic
        fun updateTermuxActivityStyling(context: Context, recreateActivity: Boolean) {
            val stylingIntent = Intent(TERMUX_ACTIVITY.ACTION_RELOAD_STYLE)
            stylingIntent.putExtra(TERMUX_ACTIVITY.EXTRA_RECREATE_ACTIVITY, recreateActivity)
            context.sendBroadcast(stylingIntent)
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    override fun bindLayout(): View {
        return layoutInflater.inflate(R.layout.activity_termux, null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.logDebug(LOG_TAG, "onCreate")
        mIsOnResumeAfterOnCreate = true

        if (savedInstanceState != null)
            mIsActivityRecreated = savedInstanceState.getBoolean(ARG_ACTIVITY_RECREATED, false)

        ReportActivity.deleteReportInfoFilesOlderThanXDays(this, 14, false)

        mProperties = TermuxAppSharedProperties.getProperties()
        reloadProperties()

        setActivityTheme()

        super.onCreate(savedInstanceState)

        mPreferences = TermuxAppSharedPreferences.build(this, true)
        if (mPreferences == null) {
            mIsInvalidState = true
            return
        }

        setMargins()

        mTermuxActivityRootView = findViewById(R.id.activity_termux_root_view)
        mTermuxActivityRootView.setActivity(this)
        mTermuxActivityBottomSpaceView = findViewById(R.id.activity_termux_bottom_space_view)
        mTermuxActivityRootView.setOnApplyWindowInsetsListener(TermuxActivityRootView.WindowInsetsListener())

        val content = findViewById<View>(android.R.id.content)
        content.setOnApplyWindowInsetsListener { v, insets ->
            mNavBarHeight = insets.systemWindowInsetBottom
            insets
        }

        if (mProperties!!.isUsingFullScreen) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        setTermuxTerminalViewAndClients()
        setTerminalToolbarView(savedInstanceState)
        setNewSessionButtonView()
        setToggleKeyboardView()

        registerForContextMenu(mTerminalView)

        // FileReceiverActivity.updateFileReceiverActivityComponentsState(this) // TODO: Migrate FileReceiverActivity

        try {
            val serviceIntent = Intent(this, TermuxService::class.java)
            startService(serviceIntent)

            if (!bindService(serviceIntent, this, 0))
                throw RuntimeException("bindService() failed")
        } catch (e: Exception) {
            Logger.logStackTraceWithMessage(LOG_TAG, "TermuxActivity failed to start TermuxService", e)
            Logger.showToast(this,
                getString(if (e.message != null && e.message!!.contains("app is in background"))
                    R.string.error_termux_service_start_failed_bg else R.string.error_termux_service_start_failed_general),
                true)
            mIsInvalidState = true
            return
        }

        TermuxUtils.sendTermuxOpenedBroadcast(this)
    }

    override fun onStart() {
        super.onStart()
        Logger.logDebug(LOG_TAG, "onStart")
        if (mIsInvalidState) return
        mIsVisible = true
        mTermuxTerminalSessionActivityClient?.onStart()
        mTermuxTerminalViewClient?.onStart()
        if (mPreferences!!.isTerminalMarginAdjustmentEnabled)
            addTermuxActivityRootViewGlobalLayoutListener()
    }

    override fun onResume() {
        super.onResume()
        Logger.logVerbose(LOG_TAG, "onResume")
        if (mIsInvalidState) return
        mTermuxTerminalSessionActivityClient?.onResume()
        mTermuxTerminalViewClient?.onResume()
        TermuxCrashUtils.notifyAppCrashFromCrashLogFile(this, LOG_TAG)
        mIsOnResumeAfterOnCreate = false
    }

    override fun onStop() {
        super.onStop()
        Logger.logDebug(LOG_TAG, "onStop")
        if (mIsInvalidState) return
        mIsVisible = false
        mTermuxTerminalSessionActivityClient?.onStop()
        mTermuxTerminalViewClient?.onStop()
        removeTermuxActivityRootViewGlobalLayoutListener()
        getDrawer().closeDrawers()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.logDebug(LOG_TAG, "onDestroy")
        mLastToast?.cancel()
        mLastToast = null
        if (mIsInvalidState) return
        mTermuxService?.unsetTermuxTerminalSessionClient()
        mTermuxService = null
        try {
            unbindService(this)
        } catch (e: Exception) {
        }
    }

    override fun onSaveInstanceState(@NonNull savedInstanceState: Bundle) {
        Logger.logVerbose(LOG_TAG, "onSaveInstanceState")
        super.onSaveInstanceState(savedInstanceState)
        saveTerminalToolbarTextInput(savedInstanceState)
        savedInstanceState.putBoolean(ARG_ACTIVITY_RECREATED, true)
    }

    override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
        Logger.logDebug(LOG_TAG, "onServiceConnected")
        mTermuxService = (service as TermuxService.LocalBinder).service

        setTermuxSessionsListView()

        val intent = intent
        setIntent(null)

        val workingDir = intent?.extras?.getString(TERMUX_ACTIVITY.EXTRA_SESSION_WORKING_DIR)
        val sessionName = intent?.extras?.getString(TERMUX_ACTIVITY.EXTRA_SESSION_NAME)
        val launchFailsafe = intent?.getBooleanExtra(TERMUX_ACTIVITY.EXTRA_FAILSAFE_SESSION, false) ?: false

        if (mTermuxService!!.isTermuxSessionsEmpty) {
            if (mIsVisible) {
                TermuxInstaller.setupBootstrapIfNeeded(this) {
                    if (mTermuxService == null) return@setupBootstrapIfNeeded
                    try {
                        setupTermuxSessionOnServiceConnected(intent, workingDir, sessionName, null, launchFailsafe)
                    } catch (e: WindowManager.BadTokenException) {
                    }
                }
            } else {
                finishActivityIfNotFinishing()
            }
        } else {
            val existingSession = if (workingDir == null) null else
                mTermuxService!!.termuxSessions.firstOrNull { it.terminalSession.cwd == workingDir }

            setupTermuxSessionOnServiceConnected(intent, workingDir, sessionName, existingSession, launchFailsafe)
        }
        mTermuxService!!.setTermuxTerminalSessionClient(mTermuxTerminalSessionActivityClient!!)
    }

    protected open fun setupTermuxSessionOnServiceConnected(
        intent: Intent?,
        workingDir: String?,
        sessionName: String?,
        existingSession: TermuxSession?,
        launchFailsafe: Boolean
    ) {
        if (mTermuxService!!.isTermuxSessionsEmpty) {
            onCreateNewSession(launchFailsafe, sessionName, workingDir)
            return
        }

        if (existingSession != null) {
            if (existingSession.executionCommand.isFailsafe != launchFailsafe) {
                onCreateNewSession(launchFailsafe, sessionName, workingDir)
            } else {
                mTermuxTerminalSessionActivityClient!!.setCurrentSession(existingSession.terminalSession)
            }
        } else if (workingDir != null) {
            onCreateNewSession(launchFailsafe, sessionName, workingDir)
        } else {
            mTermuxTerminalSessionActivityClient!!.setCurrentSession(
                mTermuxTerminalSessionActivityClient!!.getCurrentStoredSessionOrLast()
            )
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Logger.logDebug(LOG_TAG, "onServiceDisconnected")
        finishActivityIfNotFinishing()
    }

    private fun reloadProperties() {
        mProperties?.loadTermuxPropertiesFromDisk()
        mTermuxTerminalViewClient?.onReloadProperties()
    }

    private fun setActivityTheme() {
        TermuxThemeUtils.setAppNightMode(mProperties!!.getNightMode())
        AppCompatActivityUtils.setNightMode(this, NightMode.getAppNightMode().name, true)
    }

    private fun setMargins() {
        val relativeLayout = findViewById<RelativeLayout>(R.id.activity_termux_root_relative_layout)
        val marginHorizontal = mProperties!!.getTerminalMarginHorizontal()
        val marginVertical = mProperties!!.getTerminalMarginVertical()
        ViewUtils.setLayoutMarginsInDp(relativeLayout, marginHorizontal, marginVertical, marginHorizontal, marginVertical)
    }

    fun addTermuxActivityRootViewGlobalLayoutListener() {
        getTermuxActivityRootView().viewTreeObserver.addOnGlobalLayoutListener(getTermuxActivityRootView())
    }

    fun removeTermuxActivityRootViewGlobalLayoutListener() {
        getTermuxActivityRootView().viewTreeObserver.removeOnGlobalLayoutListener(getTermuxActivityRootView())
    }

    private fun setTermuxTerminalViewAndClients() {
        mTermuxTerminalSessionActivityClient = onCreateTerminalSessionClient()
        mTermuxTerminalViewClient = TermuxTerminalViewClient(this, mTermuxTerminalSessionActivityClient!!)

        mTerminalView = findViewById(R.id.terminal_view)
        mTerminalView.setTerminalViewClient(mTermuxTerminalViewClient)

        mTermuxTerminalViewClient?.onCreate()
        mTermuxTerminalSessionActivityClient?.onCreate()
    }

    @NonNull
    protected open fun onCreateTerminalSessionClient(): TermuxTerminalSessionActivityClient {
        return TermuxTerminalSessionActivityClient(this)
    }

    private fun setTermuxSessionsListView() {
        val termuxSessionsListView = findViewById<ListView>(R.id.terminal_sessions_list)
        mTermuxSessionListViewController = TermuxSessionsListViewController(this, mTermuxService!!.termuxSessions)
        termuxSessionsListView.adapter = mTermuxSessionListViewController
        termuxSessionsListView.onItemClickListener = mTermuxSessionListViewController
        termuxSessionsListView.onItemLongClickListener = mTermuxSessionListViewController
    }

    private fun setTerminalToolbarView(savedInstanceState: Bundle?) {
        mTermuxTerminalExtraKeys = TermuxTerminalExtraKeys(this, mTerminalView,
            mTermuxTerminalViewClient!!, mTermuxTerminalSessionActivityClient!!)

        val terminalToolbarViewPager = getTerminalToolbarViewPager()
        if (mPreferences!!.shouldShowTerminalToolbar()) terminalToolbarViewPager.visibility = View.VISIBLE

        val layoutParams = terminalToolbarViewPager.layoutParams
        mTerminalToolbarDefaultHeight = layoutParams.height.toFloat()

        setTerminalToolbarHeight()

        val savedTextInput = savedInstanceState?.getString(ARG_TERMINAL_TOOLBAR_TEXT_INPUT)

        terminalToolbarViewPager.adapter = TerminalToolbarViewPager.PageAdapter(this, savedTextInput)
        terminalToolbarViewPager.addOnPageChangeListener(TerminalToolbarViewPager.OnPageChangeListener(this, terminalToolbarViewPager))
    }

    private fun setTerminalToolbarHeight() {
        val terminalToolbarViewPager = getTerminalToolbarViewPager() ?: return

        val layoutParams = terminalToolbarViewPager.layoutParams
        val extraKeysInfo = mTermuxTerminalExtraKeys?.getExtraKeysInfo()
        layoutParams.height = Math.round(mTerminalToolbarDefaultHeight *
            (extraKeysInfo?.matrix?.size ?: 0) *
            mProperties!!.getTerminalToolbarHeightScaleFactor())
        terminalToolbarViewPager.layoutParams = layoutParams
    }

    fun toggleTerminalToolbar() {
        val terminalToolbarViewPager = getTerminalToolbarViewPager() ?: return

        val showNow = mPreferences!!.toogleShowTerminalToolbar()
        Logger.showToast(this, (if (showNow) getString(R.string.msg_enabling_terminal_toolbar) else getString(R.string.msg_disabling_terminal_toolbar)), true)
        terminalToolbarViewPager.visibility = if (showNow) View.VISIBLE else View.GONE
        if (showNow && isTerminalToolbarTextInputViewSelected()) {
            findViewById<EditText>(R.id.terminal_toolbar_text_input).requestFocus()
        }
    }

    private fun saveTerminalToolbarTextInput(savedInstanceState: Bundle) {
        val textInputView = findViewById<EditText>(R.id.terminal_toolbar_text_input)
        if (textInputView != null) {
            val textInput = textInputView.text.toString()
            if (textInput.isNotEmpty()) savedInstanceState.putString(ARG_TERMINAL_TOOLBAR_TEXT_INPUT, textInput)
        }
    }

    private fun setNewSessionButtonView() {
        val newSessionButton = findViewById<View>(R.id.new_session_button)
        newSessionButton.setOnClickListener { onCreateNewSession(false, null, null) }
        newSessionButton.setOnLongClickListener {
            TextInputDialogUtils.textInput(this, R.string.title_create_named_session, null,
                R.string.action_create_named_session_confirm, { text -> onCreateNewSession(false, text, null) },
                R.string.action_new_session_failsafe, { text -> onCreateNewSession(true, text, null) },
                -1, null, null)
            true
        }
    }

    protected open fun onCreateNewSession(isFailsafe: Boolean, sessionName: String?, workingDirectory: String?) {
        mTermuxTerminalSessionActivityClient!!.addNewSession(isFailsafe, sessionName, workingDirectory)
    }

    private fun setToggleKeyboardView() {
        findViewById<View>(R.id.toggle_keyboard_button).setOnClickListener {
            mTermuxTerminalViewClient!!.onToggleSoftKeyboardRequest()
            getDrawer().closeDrawers()
        }

        findViewById<View>(R.id.toggle_keyboard_button).setOnLongClickListener {
            toggleTerminalToolbar()
            true
        }
    }

    override fun onBackPressed() {
        if (getDrawer().isDrawerOpen(Gravity.LEFT)) {
            getDrawer().closeDrawers()
        } else {
            finishActivityIfNotFinishing()
        }
    }

    fun finishActivityIfNotFinishing() {
        if (!isFinishing) {
            finish()
        }
    }

    fun showToast(text: String?, longDuration: Boolean) {
        if (text.isNullOrEmpty()) return
        mLastToast?.cancel()
        mLastToast = Toast.makeText(this, text, if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
        mLastToast!!.setGravity(Gravity.TOP, 0, 0)
        mLastToast!!.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        val currentSession = getCurrentSession() ?: return

        var addAutoFillMenu = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autofillManager = getSystemService(AutofillManager::class.java)
            if (autofillManager != null && autofillManager.isEnabled) {
                addAutoFillMenu = true
            }
        }

        menu.add(Menu.NONE, CONTEXT_MENU_SELECT_URL_ID, Menu.NONE, R.string.action_select_url)
        menu.add(Menu.NONE, CONTEXT_MENU_SHARE_TRANSCRIPT_ID, Menu.NONE, R.string.action_share_transcript)
        if (!DataUtils.isNullOrEmpty(mTerminalView.storedSelectedText))
            menu.add(Menu.NONE, CONTEXT_MENU_SHARE_SELECTED_TEXT, Menu.NONE, R.string.action_share_selected_text)
        if (addAutoFillMenu)
            menu.add(Menu.NONE, CONTEXT_MENU_AUTOFILL_ID, Menu.NONE, R.string.action_autofill_password)
        menu.add(Menu.NONE, CONTEXT_MENU_RESET_TERMINAL_ID, Menu.NONE, R.string.action_reset_terminal)
        menu.add(Menu.NONE, CONTEXT_MENU_KILL_PROCESS_ID, Menu.NONE, resources.getString(R.string.action_kill_process, currentSession.pid)).isEnabled = currentSession.isRunning
        menu.add(Menu.NONE, CONTEXT_MENU_TOGGLE_KEEP_SCREEN_ON, Menu.NONE, R.string.action_toggle_keep_screen_on).isCheckable = true
        menu.findItem(CONTEXT_MENU_TOGGLE_KEEP_SCREEN_ON).isChecked = mPreferences!!.shouldKeepScreenOn()
        menu.add(Menu.NONE, CONTEXT_MENU_HELP_ID, Menu.NONE, R.string.action_open_help)
        menu.add(Menu.NONE, CONTEXT_MENU_REPORT_ID, Menu.NONE, R.string.action_report_issue)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mTerminalView.showContextMenu()
        return false
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val session = getCurrentSession()

        return when (item.itemId) {
            CONTEXT_MENU_SELECT_URL_ID -> {
                mTermuxTerminalViewClient!!.showUrlSelection()
                true
            }
            CONTEXT_MENU_SHARE_TRANSCRIPT_ID -> {
                mTermuxTerminalSessionActivityClient!!.shareSessionTranscript()
                true
            }
            CONTEXT_MENU_SHARE_SELECTED_TEXT -> {
                mTermuxTerminalSessionActivityClient!!.shareSelectedText()
                true
            }
            CONTEXT_MENU_AUTOFILL_ID -> {
                requestAutoFill()
                true
            }
            CONTEXT_MENU_RESET_TERMINAL_ID -> {
                onResetTerminalSession(session)
                true
            }
            CONTEXT_MENU_KILL_PROCESS_ID -> {
                showKillSessionDialog(session)
                true
            }
            CONTEXT_MENU_TOGGLE_KEEP_SCREEN_ON -> {
                toggleKeepScreenOn()
                true
            }
            CONTEXT_MENU_HELP_ID -> {
                ActivityUtils.startActivity(this, Intent(this, HelpActivity::class.java))
                true
            }
            CONTEXT_MENU_REPORT_ID -> {
                mTermuxTerminalViewClient!!.reportIssueFromTranscript()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onContextMenuClosed(menu: Menu) {
        super.onContextMenuClosed(menu)
        mTerminalView.onContextMenuClosed(menu)
    }

    private fun showKillSessionDialog(session: TerminalSession?) {
        if (session == null) return

        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(R.string.title_confirm_kill_process)
            .setPositiveButton(android.R.string.yes) { dialog, _ ->
                dialog.dismiss()
                session.finishIfRunning()
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    private fun onResetTerminalSession(session: TerminalSession?) {
        if (session != null) {
            session.reset()
            showToast(resources.getString(R.string.msg_terminal_reset), true)
            mTermuxTerminalSessionActivityClient?.onResetTerminalSession()
        }
    }

    private fun toggleKeepScreenOn() {
        if (mTerminalView.keepScreenOn) {
            mTerminalView.keepScreenOn = false
            mPreferences!!.setKeepScreenOn(false)
        } else {
            mTerminalView.keepScreenOn = true
            mPreferences!!.setKeepScreenOn(true)
        }
    }

    private fun requestAutoFill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autofillManager = getSystemService(AutofillManager::class.java)
            if (autofillManager != null && autofillManager.isEnabled) {
                autofillManager.requestAutofill(mTerminalView)
            }
        }
    }

    fun requestStoragePermission(isPermissionCallback: Boolean) {
        Thread {
            val requestCode = if (isPermissionCallback) -1 else PermissionUtils.REQUEST_GRANT_STORAGE_PERMISSION

            if (PermissionUtils.checkAndRequestLegacyOrManageExternalStoragePermission(
                    this, requestCode, !isPermissionCallback)) {
                if (isPermissionCallback)
                    Logger.logInfoAndShowToast(this, LOG_TAG,
                        getString(com.termux.shared.R.string.msg_storage_permission_granted_on_request))

                TermuxInstaller.setupStorageSymlinks(this)
            } else {
                if (isPermissionCallback)
                    Logger.logInfoAndShowToast(this, LOG_TAG,
                        getString(com.termux.shared.R.string.msg_storage_permission_not_granted_on_request))
            }
        }.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.logVerbose(LOG_TAG, "onActivityResult: requestCode: $requestCode, resultCode: $resultCode, data: ${IntentUtils.getIntentString(data)}")
        if (requestCode == PermissionUtils.REQUEST_GRANT_STORAGE_PERMISSION) {
            requestStoragePermission(true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Logger.logVerbose(LOG_TAG, "onRequestPermissionsResult: requestCode: $requestCode, permissions: ${Arrays.toString(permissions)}, grantResults: ${Arrays.toString(grantResults)}")
        if (requestCode == PermissionUtils.REQUEST_GRANT_STORAGE_PERMISSION) {
            requestStoragePermission(true)
        }
    }

    fun getNavBarHeight(): Int = mNavBarHeight
    fun getTermuxActivityRootView(): TermuxActivityRootView = mTermuxActivityRootView
    fun getTermuxActivityBottomSpaceView(): View? = mTermuxActivityBottomSpaceView
    fun getExtraKeysView(): ExtraKeysView? = mExtraKeysView
    fun getTermuxTerminalExtraKeys(): TermuxTerminalExtraKeys? = mTermuxTerminalExtraKeys
    fun setExtraKeysView(extraKeysView: ExtraKeysView) { mExtraKeysView = extraKeysView }
    fun getDrawer(): DrawerLayout = findViewById(R.id.drawer_layout)
    fun getTerminalToolbarViewPager(): ViewPager? = findViewById(R.id.terminal_toolbar_view_pager)
    fun getTerminalToolbarDefaultHeight(): Float = mTerminalToolbarDefaultHeight
    fun isTerminalViewSelected(): Boolean = getTerminalToolbarViewPager()?.currentItem == 0
    fun isTerminalToolbarTextInputViewSelected(): Boolean = getTerminalToolbarViewPager()?.currentItem == 1
    fun termuxSessionListNotifyUpdated() { mTermuxSessionListViewController?.notifyDataSetChanged() }
    fun isVisible(): Boolean = mIsVisible
    fun isOnResumeAfterOnCreate(): Boolean = mIsOnResumeAfterOnCreate
    fun isActivityRecreated(): Boolean = mIsActivityRecreated
    fun getTermuxService(): TermuxService? = mTermuxService
    fun getTerminalView(): TerminalView = mTerminalView
    fun getTermuxTerminalViewClient(): TermuxTerminalViewClient? = mTermuxTerminalViewClient
    fun getTermuxTerminalSessionClient(): TermuxTerminalSessionActivityClient? = mTermuxTerminalSessionActivityClient
    fun getCurrentSession(): TerminalSession? = mTerminalView.currentSession
    fun getPreferences(): TermuxAppSharedPreferences? = mPreferences
    fun getProperties(): TermuxAppSharedProperties? = mProperties
}
