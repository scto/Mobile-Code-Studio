package com.scto.mcs.feature.editor

import java.io.File

data class EditorState(
    val file: File? = null,
    val content: String = "",
    val cursorLine: Int = 0,
    val cursorColumn: Int = 0,
    val isDirty: Boolean = false
)
