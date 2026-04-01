package com.scto.mcs.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuildManager @Inject constructor(
    private val terminalEnvironment: TerminalEnvironment
) {
    private val _output = MutableSharedFlow<String>()
    val output: SharedFlow<String> = _output

    fun executeGradleTask(projectDir: File, task: String) {
        val gradlew = File(projectDir, "gradlew")
        if (!gradlew.exists()) {
            CoroutineScope(Dispatchers.IO).launch {
                _output.emit("Error: gradlew not found in ${projectDir.absolutePath}")
            }
            return
        }

        // Ensure executable permission
        gradlew.setExecutable(true)

        val processBuilder = ProcessBuilder(gradlew.absolutePath, task)
        val env = processBuilder.environment()
        env.putAll(terminalEnvironment.getEnvironmentVariables())
        
        // Set GRADLE_USER_HOME to internal storage
        val gradleHome = File(projectDir, ".gradle")
        if (!gradleHome.exists()) gradleHome.mkdirs()
        env["GRADLE_USER_HOME"] = gradleHome.absolutePath

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val process = processBuilder.redirectErrorStream(true).start()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                reader.forEachLine { line ->
                    _output.emit(line)
                }
                process.waitFor()
                _output.emit("Build finished with exit code ${process.exitValue()}")
            } catch (e: Exception) {
                _output.emit("Build failed: ${e.message}")
            }
        }
    }
}
