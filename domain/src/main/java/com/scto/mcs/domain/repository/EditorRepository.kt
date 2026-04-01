package com.scto.mcs.domain.repository

import java.io.File

interface EditorRepository {
    fun readFile(file: File): String
    fun writeFile(file: File, content: String)
    fun getLanguageConfig(file: File): String
}
