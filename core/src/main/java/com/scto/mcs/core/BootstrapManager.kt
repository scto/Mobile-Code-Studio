package com.scto.mcs.core

interface BootstrapManager {
    fun startBootstrap(
        jdkVersion: Int, 
        sdkVersion: Int, 
        onProgress: (String) -> Unit, 
        onComplete: () -> Unit
    )
    fun isEnvironmentSetup(): Boolean
}
