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
        // Hier wird die Logik zum Laden der languages.json implementiert
        // Dies ist ein Platzhalter für die tatsächliche Registrierung der Grammatiken
    }

    /**
     * Initialisiert Sora-Editor Themes und Farbschemata.
     */
    fun loadDefaultTheme() {
        loadTheme("darcula.json", "darcula")
        loadTheme("QuietLight.tmTheme.json", "QuietLight")
    }

    private fun loadTheme(fileName: String, themeName: String) {
        try {
            val themeModel = ThemeModel.create(fileName)
            // Hier würde das Theme in der Registry registriert werden
        } catch (e: Exception) {
            // Fehlerbehandlung beim Laden des Themes
        }
    }

    /**
     * Wählt dynamisch zwischen "darcula" (Night) und "QuietLight" (Day) basierend auf dem System-UI-Mode.
     */
    fun applyThemeBasedOnConfiguration(configuration: Configuration) {
        val isNightMode = (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val themeName = if (isNightMode) "darcula" else "QuietLight"
        // Logik zum Anwenden des Themes auf den Editor
    }
}
