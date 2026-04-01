package com.scto.mcs.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalSessionManager @Inject constructor(
    private val terminalEnvironment: TerminalEnvironment
) {

    private var process: Process? = null
    private var writer: BufferedWriter? = null
    private val _output = MutableSharedFlow<String>()
    val output: SharedFlow<String> = _output

    fun startSession() {
        val env = terminalEnvironment.getEnvironmentVariables()
        val processBuilder = ProcessBuilder("/system/bin/sh")
        val environment = processBuilder.environment()
        environment.putAll(env)
        
        process = processBuilder.redirectErrorStream(true).start()
        writer = BufferedWriter(OutputStreamWriter(process!!.outputStream))
        
        CoroutineScope(Dispatchers.IO).launch {
            val reader = BufferedReader(InputStreamReader(process!!.inputStream))
            reader.forEachLine { line ->
                _output.emit(line)
            }
        }
    }

    fun execute(command: String) {
        writer?.write("$command\n")
        writer?.flush()
    }

    fun stopSession() {
        process?.destroy()
    }
}
