package com.scto.mcs.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalEnvironment @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val homeDir: File get() = File(context.filesDir, "home")
    val usrBinDir: File get() = File(homeDir, "usr/bin")
    val tmpDir: File get() = File(context.cacheDir, "tmp")

    init {
        homeDir.mkdirs()
        usrBinDir.mkdirs()
        tmpDir.mkdirs()
    }

    fun getEnv(): Map<String, String> {
        return mapOf(
            "HOME" to homeDir.absolutePath,
            "PATH" to "${usrBinDir.absolutePath}:/system/bin",
            "JAVA_HOME" to "/system/lib/jvm",
            "ANDROID_HOME" to "/system/lib/android"
        )
    }
}
