package com.scto.mcs.core

import android.content.Context
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalEnvironment @Inject constructor() {

    var homePath: String = ""
    var jdkPath: String = ""
    var sdkPath: String = ""

    fun initBootstrap(context: Context) {
        val filesDir = context.filesDir
        val homeDir = File(filesDir, "home")
        if (!homeDir.exists()) {
            homeDir.mkdirs()
        }
        homePath = homeDir.absolutePath
        jdkPath = File(filesDir, "jdk").absolutePath
        sdkPath = File(filesDir, "sdk").absolutePath
    }

    fun getEnvironmentVariables(): Map<String, String> {
        return mapOf(
            "HOME" to homePath,
            "JAVA_HOME" to jdkPath,
            "ANDROID_HOME" to sdkPath,
            "PATH" to "$homePath/bin:/system/bin:/system/xbin",
            "TMPDIR" to "$homePath/tmp",
            "LD_LIBRARY_PATH" to "$homePath/lib"
        )
    }
}
