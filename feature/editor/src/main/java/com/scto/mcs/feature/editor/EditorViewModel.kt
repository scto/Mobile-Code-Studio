package com.scto.mcs.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class EditorViewModel : ViewModel() {

    private val _fileContent = MutableStateFlow("")
    val fileContent: StateFlow<String> = _fileContent

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
        }
    }
}
