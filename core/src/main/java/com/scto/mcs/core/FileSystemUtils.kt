package com.scto.mcs.core

import com.scto.mcs.core.constants.TermuxConstants
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileSystemUtils @Inject constructor() {
    val rootDir: File get() = File(TermuxConstants.HOME, "mobilecodestudio")

    init {
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
    }

    fun getFile(path: String): File {
        return File(rootDir, path)
    }
}
