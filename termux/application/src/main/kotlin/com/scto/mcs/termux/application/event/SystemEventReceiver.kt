package com.scto.mcs.termux.application.event

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import com.scto.mcs.termux.shared.data.IntentUtils
import com.scto.mcs.termux.shared.logger.Logger
import com.scto.mcs.termux.shared.termux.TermuxUtils
import com.scto.mcs.termux.shared.termux.file.TermuxFileUtils
import com.scto.mcs.termux.shared.termux.shell.TermuxShellManager
import com.scto.mcs.termux.shared.termux.shell.command.environment.TermuxShellEnvironment

class SystemEventReceiver : BroadcastReceiver() {

    companion object {
        private var mInstance: SystemEventReceiver? = null
        private const val LOG_TAG = "SystemEventReceiver"

        @Synchronized
        fun getInstance(): SystemEventReceiver {
            if (mInstance == null) {
                mInstance = SystemEventReceiver()
            }
            return mInstance!!
        }

        @Synchronized
        fun registerPackageUpdateEvents(context: Context) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
            intentFilter.addDataScheme("package")
            context.registerReceiver(getInstance(), intentFilter)
        }

        @Synchronized
        fun unregisterPackageUpdateEvents(context: Context) {
            context.unregisterReceiver(getInstance())
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        Logger.logDebug(LOG_TAG, "Intent Received:\n" + IntentUtils.getIntentString(intent))

        val action = intent.action ?: return

        when (action) {
            Intent.ACTION_BOOT_COMPLETED -> onActionBootCompleted(context, intent)
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_REPLACED -> onActionPackageUpdated(context, intent)
            else -> Logger.logError(LOG_TAG, "Invalid action \"$action\" passed to $LOG_TAG")
        }
    }

    @Synchronized
    fun onActionBootCompleted(context: Context, intent: Intent) {
        TermuxShellManager.onActionBootCompleted(context, intent)
    }

    @Synchronized
    fun onActionPackageUpdated(context: Context, intent: Intent) {
        val data = intent.data
        if (data != null && TermuxUtils.isUriDataForTermuxPluginPackage(data)) {
            Logger.logDebug(LOG_TAG, intent.action!!.replace("^android.intent.action.".toRegex(), "") +
                " event received for \"" + data.toString().replace("^package:".toRegex(), "") + "\"")
            if (TermuxFileUtils.isTermuxFilesDirectoryAccessible(context, false, false) == null)
                TermuxShellEnvironment.writeEnvironmentToFile(context)
        }
    }
}
