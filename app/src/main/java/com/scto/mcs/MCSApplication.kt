package com.scto.mcs

import android.app.Application
import com.scto.mcs.core.EditorConfigManager
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

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        // Initialisierung im Hintergrund, um den App-Start nicht zu blockieren
        applicationScope.launch {
            // TerminalEnvironment.initFolders() wird bereits im init-Block des Singletons aufgerufen
            // Wir stellen hier sicher, dass die Konfiguration geladen wird
            editorConfigManager.preloadGrammars()
            editorConfigManager.loadDefaultTheme()
        }
    }
}
