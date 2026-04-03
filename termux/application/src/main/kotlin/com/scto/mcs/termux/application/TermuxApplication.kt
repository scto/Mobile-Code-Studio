package com.scto.mcs.termux.application

import android.content.Context
import com.tom.rv2ide.app.BaseApplication
import com.scto.mcs.termux.shared.errors.Error
import com.scto.mcs.termux.shared.logger.Logger
import com.scto.mcs.termux.shared.termux.TermuxConstants
import com.scto.mcs.termux.shared.termux.file.TermuxFileUtils
import com.scto.mcs.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences
import com.scto.mcs.termux.shared.termux.settings.properties.TermuxAppSharedProperties
import com.scto.mcs.termux.shared.termux.shell.TermuxShellManager
import com.scto.mcs.termux.shared.termux.shell.am.TermuxAmSocketServer
import com.scto.mcs.termux.shared.termux.shell.command.environment.TermuxShellEnvironment
import com.scto.mcs.termux.shared.termux.theme.TermuxThemeUtils

class TermuxApplication : BaseApplication() {

    companion object {
        private const val LOG_TAG = "TermuxApplication"

        fun setLogConfig(context: Context) {
            Logger.setDefaultLogTag(TermuxConstants.TERMUX_APP_NAME)

            // Load the log level from shared preferences and set it to the Logger.CURRENT_LOG_LEVEL
            val preferences = TermuxAppSharedPreferences.build(context) ?: return
            preferences.setLogLevel(null, preferences.getLogLevel())
        }
    }

    override fun onCreate() {
        super.onCreate()

        val context = applicationContext

        // Set log config for the app
        setLogConfig(context)

        Logger.logDebug("Starting Application")

        // Init app wide SharedProperties loaded from termux.properties
        val properties = TermuxAppSharedProperties.init(context)

        // Init app wide shell manager
        TermuxShellManager.init(context)

        // Set NightMode.APP_NIGHT_MODE
        TermuxThemeUtils.setAppNightMode(properties.getNightMode())

        // Check and create termux files directory. If failed to access it like in case of secondary
        // user or external sd card installation, then don't run files directory related code
        val error = TermuxFileUtils.isTermuxFilesDirectoryAccessible(this, true, true)
        val isTermuxFilesDirectoryAccessible = error == null
        if (isTermuxFilesDirectoryAccessible) {
            Logger.logInfo(LOG_TAG, "Termux files directory is accessible")

            val appsDirError = TermuxFileUtils.isAppsTermuxAppDirectoryAccessible(true, true)
            if (appsDirError != null) {
                Logger.logErrorExtended(LOG_TAG, "Create apps/termux-app directory failed\n$appsDirError")
                return
            }

            // Setup termux-am-socket server
            TermuxAmSocketServer.setupTermuxAmSocketServer(context)
        } else {
            Logger.logErrorExtended(LOG_TAG, "Termux files directory is not accessible\n$error")
        }

        // Init TermuxShellEnvironment constants and caches after everything has been setup including termux-am-socket server
        TermuxShellEnvironment.init(this)

        if (isTermuxFilesDirectoryAccessible) {
            TermuxShellEnvironment.writeEnvironmentToFile(this)
        }
    }
}
