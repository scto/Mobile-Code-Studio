package com.scto.mcs.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.EditorConfigManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class EditorViewModel(private val configManager: EditorConfigManager) : ViewModel() {

    private val _fileContent = MutableStateFlow("")
    val fileContent: StateFlow<String> = _fileContent

    private val _isDirty = MutableStateFlow(false)
    val isDirty: StateFlow<Boolean> = _isDirty

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

    fun getConfigManager() = configManager
}
