package com.scto.mcs.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.GitCallback
import com.scto.mcs.core.GitManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

data class CloneUiState(
    val isLoading: Boolean = false,
    val progress: String = "",
    val error: String? = null,
    val isSuccess: Boolean = false
)

class CloneViewModel(private val gitManager: GitManager) : ViewModel() {

    private val _uiState = MutableStateFlow(CloneUiState())
    val uiState: StateFlow<CloneUiState> = _uiState

    fun cloneRepository(url: String, targetPath: String) {
        _uiState.value = CloneUiState(isLoading = true, progress = "Starting...")
        
        viewModelScope.launch {
            gitManager.clone(url, File(targetPath), null, object : GitCallback {
                override fun onProgress(message: String) {
                    _uiState.value = _uiState.value.copy(progress = message)
                }

                override fun onError(error: String) {
                    _uiState.value = CloneUiState(isLoading = false, error = error)
                }

                override fun onSuccess() {
                    _uiState.value = CloneUiState(isLoading = false, isSuccess = true)
                }
            })
        }
    }
}
