package com.scto.mcs.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditorConfigManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    init {
        FileProviderRegistry.getInstance().setRegistry(AssetsFileResolver(context.assets))
    }

    suspend fun loadDefaultThemes() = withContext(Dispatchers.IO) {
        // Lädt Themes aus editor/schemes/
        loadTheme("editor/schemes/darcula.json", "darcula")
        loadTheme("editor/schemes/quietlight.json", "quietlight")
    }

    private fun loadTheme(path: String, name: String) {
        try {
            val themeModel = ThemeModel.create(path)
            // Registrierung in der Sora-Editor Registry
            io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry.getInstance().loadTheme(name, themeModel)
        } catch (e: Exception) {
            // Fehlerbehandlung
        }
    }
}
