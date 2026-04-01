package com.scto.mcs.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.BuildManager
import com.scto.mcs.core.EditorConfigManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val configManager: EditorConfigManager,
    private val buildManager: BuildManager
) : ViewModel() {

    private val _fileContent = MutableStateFlow("")
    val fileContent: StateFlow<String> = _fileContent

    private val _isDirty = MutableStateFlow(false)
    val isDirty: StateFlow<Boolean> = _isDirty

    val buildOutput = buildManager.output

    fun loadFile(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            if (file.exists()) {
                _fileContent.value = file.readText()
            }
        }
    }

    fun saveFile(file: File, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            file.writeText(content)
            _isDirty.value = false
        }
    }

    fun onTextChanged(newText: String) {
        _isDirty.value = newText != _fileContent.value
    }

    fun buildProject(projectDir: File) {
        buildManager.executeGradleTask(projectDir, "assembleDebug")
    }

    fun getConfigManager() = configManager
}
