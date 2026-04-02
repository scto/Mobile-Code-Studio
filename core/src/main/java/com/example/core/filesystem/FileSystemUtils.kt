package com.example.core.filesystem

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileSystemUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val rootDir: File = File(context.filesDir, "mobilecodestudio")

    init {
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
    }

    fun getFile(path: String): File {
        return File(rootDir, path)
    }

    fun listFiles(path: String = ""): Array<File>? {
        return File(rootDir, path).listFiles()
    }

    fun writeToFile(path: String, content: String) {
        File(rootDir, path).writeText(content)
    }

    fun readFromFile(path: String): String {
        return File(rootDir, path).readText()
    }
}
