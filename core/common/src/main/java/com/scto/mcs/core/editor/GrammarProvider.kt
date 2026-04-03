package com.scto.mcs.core.editor

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scto.mcs.core.editor.model.GrammarModel
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.GrammarModel as SoraGrammarModel
import java.io.InputStreamReader

object GrammarProvider {
    private val gson = Gson()
    private var grammars: List<GrammarModel> = emptyList()

    fun initialize(context: Context) {
        try {
            context.assets.open("editor/textmate/grammars.json").use { inputStream ->
                val type = object : TypeToken<List<GrammarModel>>() {}.type
                grammars = gson.fromJson(InputStreamReader(inputStream), type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun findScopeByFileExtension(extension: String): String? {
        return grammars.find { it.fileTypes.contains(extension) }?.scopeName
    }

    fun registerGrammar(scopeName: String) {
        val grammar = grammars.find { it.scopeName == scopeName } ?: return
        val model = SoraGrammarModel(
            grammar.name,
            grammar.scopeName,
            grammar.path,
            grammar.embeddedLanguages
        )
        GrammarRegistry.getInstance().loadGrammar(model)
    }
}
