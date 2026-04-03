package com.scto.mcs.core.editor.model

data class GrammarModel(
    val name: String,
    val scopeName: String,
    val path: String,
    val fileTypes: List<String>,
    val embeddedLanguages: Map<String, String>? = null
)
