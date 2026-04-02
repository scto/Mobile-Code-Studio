package com.example.core.terminal

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalEnvironment @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val homeDir: File = File(context.filesDir, "home")
    val binDir: File = File(context.filesDir, "usr/bin")
    val tmpDir: File = File(context.filesDir, "tmp")

    // Environment variables
    val path: String
        get() = "${binDir.absolutePath}:/system/bin:/system/xbin"
    
    val javaHome: String
        get() = File(context.filesDir, "java").absolutePath
        
    val androidHome: String
        get() = File(context.filesDir, "android").absolutePath

    init {
        // Ensure the directory structure exists
        homeDir.mkdirs()
        binDir.mkdirs()
        tmpDir.mkdirs()
    }
}
