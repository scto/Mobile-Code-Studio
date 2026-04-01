package com.scto.mcs.core

import android.content.Context
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileSystemUtils @Inject constructor(private val context: Context) {

    fun getRootDirectory(): File {
        return File(context.filesDir, "mobilecodestudio")
    }

    fun readFile(file: File): String = file.readText()

    fun writeFile(file: File, content: String) = file.writeText(content)

    fun deleteFile(file: File) = file.delete()

    fun listFiles(dir: File): List<File> = dir.listFiles()?.toList() ?: emptyList()
}
