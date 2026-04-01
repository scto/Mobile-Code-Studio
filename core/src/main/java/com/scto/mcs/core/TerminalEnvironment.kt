package com.scto.mcs.core

import android.content.Context
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalEnvironment @Inject constructor() {

    var homePath: String = ""
    var usrBinPath: String = ""
    var tmpPath: String = ""
    var jdkPath: String = ""
    var sdkPath: String = ""

    fun initBootstrap(context: Context) {
        val filesDir = context.filesDir
        val homeDir = File(filesDir, "home")
        val usrBinDir = File(filesDir, "usr/bin")
        val tmpDir = File(filesDir, "tmp")
        
        if (!homeDir.exists()) homeDir.mkdirs()
        if (!usrBinDir.exists()) usrBinDir.mkdirs()
        if (!tmpDir.exists()) tmpDir.mkdirs()
        
        homePath = homeDir.absolutePath
        usrBinPath = usrBinDir.absolutePath
        tmpPath = tmpDir.absolutePath
        jdkPath = File(filesDir, "jdk").absolutePath
        sdkPath = File(filesDir, "sdk").absolutePath
    }

    fun getEnvironmentVariables(): Map<String, String> {
        return mapOf(
            "HOME" to homePath,
            "JAVA_HOME" to jdkPath,
            "ANDROID_HOME" to sdkPath,
            "PATH" to "$usrBinPath:$jdkPath/bin:$sdkPath/bin:/system/bin:/system/xbin",
            "TMPDIR" to tmpPath,
            "LD_LIBRARY_PATH" to "$homePath/lib"
        )
    }
}
