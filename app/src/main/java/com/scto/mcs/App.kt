package com.scto.mcs

import android.app.Application
import android.content.Context
import com.scto.mcs.core.BootstrapManager
import com.scto.mcs.core.TerminalEnvironment
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), BootstrapManager {

    @Inject lateinit var terminalEnvironment: TerminalEnvironment
    private val prefs by lazy { getSharedPreferences("mcs_prefs", Context.MODE_PRIVATE) }

    override fun onCreate() {
        super.onCreate()
        terminalEnvironment.initBootstrap(this)
    }

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
                // Simulate installation
                onProgress("Downloading JDK $jdkVersion...")
                Thread.sleep(1000)
                onProgress("Extracting JDK $jdkVersion...")
                Thread.sleep(1000)
                onProgress("Downloading Android SDK $sdkVersion...")
                Thread.sleep(1000)
                onProgress("Extracting Android SDK $sdkVersion...")
                Thread.sleep(1000)
                onProgress("Bootstrap complete.")

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
