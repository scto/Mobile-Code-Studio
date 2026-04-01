package com.scto.mcs

import android.app.Application
import com.scto.mcs.core.EditorConfigManager
import com.scto.mcs.core.TerminalEnvironment
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class MobileCodeStudioApp : Application() {

    @Inject lateinit var terminalEnvironment: TerminalEnvironment
    @Inject lateinit var editorConfigManager: EditorConfigManager

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        
        terminalEnvironment.initBootstrap(this)
        editorConfigManager.preloadGrammars()
    }
}
