package com.scto.mcs.feature.debug

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogcatManager @Inject constructor() {

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs

    private var process: Process? = null
    private var isRunning = false

    fun startLogging() {
        if (isRunning) return
        isRunning = true
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                process = ProcessBuilder("logcat", "-v", "long").start()
                val reader = BufferedReader(InputStreamReader(process!!.inputStream))
                
                reader.forEachLine { line ->
                    // Simple parsing for demonstration
                    // In reality, logcat -v long output is multi-line
                    val parts = line.split(" ")
                    if (parts.size >= 5) {
                        val entry = LogEntry(
                            timestamp = parts[0],
                            level = parts[1],
                            tag = parts[2],
                            pid = parts[3],
                            message = parts.drop(4).joinToString(" ")
                        )
                        _logs.value = _logs.value + entry
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopLogging() {
        isRunning = false
        process?.destroy()
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }
}
