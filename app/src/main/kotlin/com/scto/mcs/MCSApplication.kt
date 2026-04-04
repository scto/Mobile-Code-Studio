package com.scto.mcs

import android.app.Application
import com.scto.mcs.core.CrashHandler
import com.scto.mcs.core.EventManager
import com.scto.mcs.core.editor.GrammarProvider
import dagger.hilt.android.HiltAndroidApp
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import javax.inject.Inject

@HiltAndroidApp
class MCSApplication : Application() {

    @Inject lateinit var crashHandler: CrashHandler
    @Inject lateinit var eventManager: EventManager

    companion object {
        lateinit var instance: MCSApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)
        eventManager.clearListeners()
        
        GrammarProvider.initialize(this)
        loadDefaultThemes()
    }

    private fun loadDefaultThemes() {
        FileProviderRegistry.getInstance().addFileProvider(AssetsFileResolver(assets))
        
        val themes = listOf("darcula", "quietlight", "abyss", "solarized_drak")
        themes.forEach { name ->
            try {
                val themeModel = ThemeModel.create("editor/schemes/$name.json")
                themeModel.isDark = name != "quietlight"
                ThemeRegistry.getInstance().loadTheme(name, themeModel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
