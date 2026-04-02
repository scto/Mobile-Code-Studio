package com.scto.mcs.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.TerminalEnvironment
import com.scto.mcs.domain.repository.EditorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val editorRepository: EditorRepository,
    private val terminalEnvironment: TerminalEnvironment
) : ViewModel() {

    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state

    private val _buildOutput = MutableStateFlow("")
    val buildOutput: StateFlow<String> = _buildOutput

    fun loadFile(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            val content = editorRepository.readFile(file)
            _state.value = _state.value.copy(file = file, content = content, isDirty = false)
        }
    }

    fun saveFile(content: String) {
        val file = _state.value.file ?: return
        editorRepository.saveFile(file, content)
        _state.value = _state.value.copy(content = content, isDirty = false)
    }

    fun updateContent(content: String) {
        _state.value = _state.value.copy(content = content, isDirty = true)
    }

    fun runBuild(projectDir: File) {
        viewModelScope.launch(Dispatchers.IO) {
            _buildOutput.value = "Starting build in ${projectDir.absolutePath}..."
            
            try {
                val processBuilder = ProcessBuilder("./gradlew", "assembleDebug")
                processBuilder.directory(projectDir)
                
                // Set environment variables
                val env = processBuilder.environment()
                env.putAll(terminalEnvironment.getEnv())
                
                val process = processBuilder.start()
                val output = process.inputStream.bufferedReader().readText()
                val error = process.errorStream.bufferedReader().readText()
                
                val exitCode = process.waitFor()
                
                _buildOutput.value = if (exitCode == 0) {
                    "Build successful!\n$output"
                } else {
                    "Build failed (Exit $exitCode):\n$error"
                }
            } catch (e: Exception) {
                _buildOutput.value = "Build error: ${e.message}"
            }
        }
    }
}
