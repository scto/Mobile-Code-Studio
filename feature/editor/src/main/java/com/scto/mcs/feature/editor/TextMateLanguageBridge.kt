package com.scto.mcs.feature.editor

import com.scto.mcs.core.editor.GrammarProvider
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import java.io.File

object TextMateLanguageBridge {
    fun create(file: File): TextMateLanguage {
        val extension = file.extension
        val scopeName = GrammarProvider.findScopeByFileExtension(extension) ?: "source.java"
        GrammarProvider.registerGrammar(scopeName)
        
        return TextMateLanguage.create(
            scopeName,
            true
        )
    }
}
