package com.scto.mcs.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogcatManager @Inject constructor() {

    private val _output = MutableSharedFlow<String>()
    val output: SharedFlow<String> = _output

    private var process: Process? = null

    fun startLogging() {
        CoroutineScope(Dispatchers.IO).launch {
            process = ProcessBuilder("logcat").start()
            val reader = BufferedReader(InputStreamReader(process!!.inputStream))
            reader.forEachLine { line ->
                _output.emit(line)
            }
        }
    }

    fun stopLogging() {
        process?.destroy()
    }
}
