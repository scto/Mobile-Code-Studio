package com.scto.mcs

package com.scto.mcs

import android.app.Application
import com.scto.mcs.core.EditorConfigManager
import com.scto.mcs.core.TerminalEnvironment
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var terminalEnvironment: TerminalEnvironment
    @Inject lateinit var editorConfigManager: EditorConfigManager

    override fun onCreate() {
        super.onCreate()
        terminalEnvironment.initBootstrap(this)
        editorConfigManager.preloadGrammars()
    }
}
