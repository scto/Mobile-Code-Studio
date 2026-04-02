package com.scto.mcs.core

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashHandler @Inject constructor() {

    fun init() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("MCS_CRASH", "Uncaught exception in thread ${thread.name}", throwable)
            // Hier könnte eine Logik zur Speicherung des Crash-Logs folgen
        }
    }
}
