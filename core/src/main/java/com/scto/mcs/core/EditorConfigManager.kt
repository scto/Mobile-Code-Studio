package com.scto.mcs.core

import android.content.Context
import androidx.compose.material3.ColorScheme
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditorConfigManager @Inject constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    fun preloadGrammars() {
        scope.launch {
            // Logic to load TextMate grammars from assets/grammar
        }
    }

    fun syncWithComposeTheme(isDark: Boolean, colorScheme: ColorScheme): EditorColorScheme {
        return EditorColorScheme().apply {
            setColor(EditorColorScheme.TEXT_NORMAL, colorScheme.onSurface.hashCode())
            setColor(EditorColorScheme.BACKGROUND, colorScheme.surface.hashCode())
        }
    }

    fun getLanguageForFile(file: File): Language {
        return when (file.extension) {
            "kt" -> TextMateLanguage.create("source.kotlin", true)
            "java" -> TextMateLanguage.create("source.java", true)
            else -> TextMateLanguage.create("text.plain", true)
        }
    }
}
