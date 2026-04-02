package com.scto.mcs

import android.app.Application
import com.scto.mcs.core.CrashHandler
import com.scto.mcs.core.EditorConfigManager
import com.scto.mcs.core.EventManager
import com.scto.mcs.core.TerminalEnvironment
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MCSApplication : Application() {

    @Inject lateinit var terminalEnvironment: TerminalEnvironment
    @Inject lateinit var editorConfigManager: EditorConfigManager
    @Inject lateinit var crashHandler: CrashHandler
    @Inject lateinit var eventManager: EventManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        // CrashHandler initialisieren
        crashHandler.init()

        // Initialisierung im Hintergrund
        applicationScope.launch {
            editorConfigManager.loadDefaultThemes()
        }
    }
}
