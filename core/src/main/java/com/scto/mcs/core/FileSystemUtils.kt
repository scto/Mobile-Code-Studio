package com.scto.mcs.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileSystemUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val rootDir: File get() = File(context.filesDir, "mobilecodestudio")

    init {
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
    }

    fun getFile(path: String): File {
        return File(rootDir, path)
    }
}
