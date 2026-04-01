package com.scto.mcs

import android.app.Application
import android.content.Context
import com.scto.mcs.core.BootstrapManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application(), BootstrapManager {

    private val prefs by lazy { getSharedPreferences("mcs_prefs", Context.MODE_PRIVATE) }

    override fun isEnvironmentSetup(): Boolean {
        return prefs.getBoolean("is_bootstrapped", false)
    }

    override fun startBootstrap(
        jdkVersion: Int, 
        sdkVersion: Int, 
        onProgress: (String) -> Unit, 
        onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            onProgress("Initializing Termux environment...")
            onProgress("Selected JDK: $jdkVersion")
            onProgress("Selected Android SDK: $sdkVersion")
            
            try {
                // Simulate environment setup
                val process = ProcessBuilder("/system/bin/sh").redirectErrorStream(true).start()
                val writer = process.outputStream.bufferedWriter()
                val reader = process.inputStream.bufferedReader()

                writer.write("echo 'Setting up environment variables...'\n")
                writer.write("export JAVA_HOME=/data/data/com.scto.mcs/files/jdk-$jdkVersion\n")
                writer.write("export ANDROID_HOME=/data/data/com.scto.mcs/files/sdk-$sdkVersion\n")
                writer.write("export PATH=\$PATH:\$JAVA_HOME/bin:\$ANDROID_HOME/bin\n")
                writer.write("echo 'Installing JDK $jdkVersion...'\n")
                writer.write("sleep 1\n")
                writer.write("echo 'Installing Android SDK $sdkVersion...'\n")
                writer.write("sleep 1\n")
                writer.write("echo 'Bootstrap complete.'\n")
                writer.write("exit\n")
                writer.flush()
                
                reader.forEachLine { line ->
                    onProgress(line)
                }
                
                process.waitFor()

                // Save state
                prefs.edit()
                    .putBoolean("is_bootstrapped", true)
                    .putInt("jdk_version", jdkVersion)
                    .putInt("sdk_version", sdkVersion)
                    .apply()

                onComplete()
            } catch (e: Exception) {
                onProgress("Error during bootstrap: ${e.message}")
            }
        }
    }
}
