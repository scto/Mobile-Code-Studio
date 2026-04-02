package com.scto.mcs.feature.editor

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Core engine responsible for managing the IDE state,
 * file operations, and syntax highlighting logic.
 */
class EditorEngine {

    private val _codeContent = MutableStateFlow("")
    val codeContent: StateFlow<String> = _codeContent

    fun initialize() {
        // Setup initial state for the editor
        _codeContent.value = "// Welcome to Mobile Code Studio\nfun main() {\n    println(\"Hello World\")\n}"
    }

    fun updateCode(newCode: String) {
        _codeContent.value = newCode
    }
}
