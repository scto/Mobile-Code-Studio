package com.scto.mcs.core

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashHandler @Inject constructor() : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Log.e("MCS_CRASH", "Uncaught exception in thread ${thread.name}", throwable)
    }
}
