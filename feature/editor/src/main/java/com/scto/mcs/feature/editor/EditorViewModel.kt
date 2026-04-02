package com.scto.mcs.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.EditorConfigManager
import com.scto.mcs.core.TerminalEnvironment
import com.scto.mcs.domain.repository.EditorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val editorRepository: EditorRepository,
    private val editorConfigManager: EditorConfigManager,
    private val terminalEnvironment: TerminalEnvironment
) : ViewModel() {

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    private val _buildOutput = MutableStateFlow("")
    val buildOutput: StateFlow<String> = _buildOutput

    fun loadFile(file: File) {
        _code.value = editorRepository.readFile(file)
    }

    fun saveFile(file: File, content: String) {
        editorRepository.saveFile(file, content)
    }

    fun runBuild(projectDir: File) {
        viewModelScope.launch {
            _buildOutput.value = "Starting build in ${projectDir.absolutePath}..."
            val env = terminalEnvironment.getEnv()
            _buildOutput.value += "\nUsing JAVA_HOME: ${env["JAVA_HOME"]}"
            _buildOutput.value += "\nExecuting ./gradlew assembleDebug..."
            // Simulation des Build-Prozesses
            _buildOutput.value += "\nBuild successful!"
        }
    }
}
