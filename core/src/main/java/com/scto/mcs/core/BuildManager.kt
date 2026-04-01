package com.scto.mcs.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val terminalEnvironment: TerminalEnvironment,
    @ApplicationContext private val context: Context
) {
    private val _output = MutableSharedFlow<String>()
    val output: SharedFlow<String> = _output

    fun executeGradleTask(projectDir: File, task: String) {
        CoroutineScope(Dispatchers.IO).launch {
            ensureGradleWrapper(projectDir)
            configureGradleProperties(projectDir)

            val gradlew = File(projectDir, "gradlew")
            gradlew.setExecutable(true)

            val processBuilder = ProcessBuilder(gradlew.absolutePath, task)
            val env = processBuilder.environment()
            env.putAll(terminalEnvironment.getEnvironmentVariables())
            
            val gradleHome = File(projectDir, ".gradle")
            if (!gradleHome.exists()) gradleHome.mkdirs()
            env["GRADLE_USER_HOME"] = gradleHome.absolutePath

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

    private fun ensureGradleWrapper(projectDir: File) {
        val gradlew = File(projectDir, "gradlew")
        if (!gradlew.exists()) {
            // In a real app, copy from assets/gradle-wrapper/
            // For now, we simulate this by creating a dummy script
            gradlew.writeText("#!/bin/sh\necho 'Gradle wrapper injected'")
            gradlew.setExecutable(true)
        }
    }

    private fun configureGradleProperties(projectDir: File) {
        val props = File(projectDir, "gradle.properties")
        val jvmArgs = "org.gradle.jvmargs=-Xmx1024m"
        
        if (!props.exists()) {
            props.writeText(jvmArgs)
        } else {
            val content = props.readText()
            if (!content.contains("org.gradle.jvmargs")) {
                props.appendText("\n$jvmArgs")
            }
        }
    }
}
