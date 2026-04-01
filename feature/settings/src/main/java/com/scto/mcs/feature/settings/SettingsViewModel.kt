package com.scto.mcs.feature.settings

import androidx.lifecycle.ViewModel
import com.scto.mcs.core.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.value = SettingsUiState(
            theme = repository.getString("theme", "System"),
            fontSize = repository.getInt("font_size", 14),
            shellPath = repository.getString("shell_path", "/system/bin/sh"),
            jdkPath = repository.getString("jdk_path", "/data/data/com.scto.mcs/files/jdk-17")
        )
    }

    fun updateTheme(theme: String) {
        repository.setString("theme", theme)
        _uiState.value = _uiState.value.copy(theme = theme)
    }
}

data class SettingsUiState(
    val theme: String = "System",
    val fontSize: Int = 14,
    val shellPath: String = "/system/bin/sh",
    val jdkPath: String = ""
)
