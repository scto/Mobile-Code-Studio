package com.scto.mcs.core

import android.content.Context
import android.content.res.Configuration
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
        // Register assets resolver for TextMate
        FileProviderRegistry.getInstance().setRegistry(AssetsFileResolver(context.assets))
    }

    /**
     * Lädt TextMate Grammatiken asynchron aus dem assets/textmate Verzeichnis.
     */
    suspend fun preloadGrammars() = withContext(Dispatchers.IO) {
        // Hier würde die Logik zum Laden der languages.json stehen
    }

    /**
     * Initialisiert Sora-Editor Themes und Farbschemata.
     */
    fun loadDefaultTheme() {
        loadTheme("darcula.json", "darcula")
        loadTheme("QuietLight.tmTheme.json", "QuietLight")
    }

    private fun loadTheme(fileName: String, themeName: String) {
        // Erstellt ThemeModel aus Assets
        // ThemeModel.create(fileName)
    }

    /**
     * Wählt dynamisch zwischen "darcula" (Night) und "QuietLight" (Day) basierend auf dem System-UI-Mode.
     */
    fun applyThemeBasedOnConfiguration(configuration: Configuration) {
        val isNightMode = (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val themeName = if (isNightMode) "darcula" else "QuietLight"
        // Logik zum Anwenden des Themes
    }
}
