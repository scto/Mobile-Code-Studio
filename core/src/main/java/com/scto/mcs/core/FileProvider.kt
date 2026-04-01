package com.scto.mcs.core

import android.content.Context
import java.io.File

class FileProvider(private val context: Context) {

    fun getAppFilesDir(): File {
        return context.filesDir
    }

    fun createFile(fileName: String): File {
        return File(context.filesDir, fileName)
    }
}
