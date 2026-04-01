package com.scto.mcs.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.GitCallback
import com.scto.mcs.core.GitManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class CloneState {
    object Idle : CloneState()
    data class Cloning(val progress: Float) : CloneState()
    data class Success(val path: String) : CloneState()
    data class Error(val message: String) : CloneState()
}

class DashboardViewModel(private val gitManager: GitManager) : ViewModel() {

    private val _cloneState = MutableStateFlow<CloneState>(CloneState.Idle)
    val cloneState: StateFlow<CloneState> = _cloneState

    fun cloneRepository(url: String, targetPath: String) {
        _cloneState.value = CloneState.Cloning(0f)
        
        viewModelScope.launch {
            gitManager.clone(url, File(targetPath), null, object : GitCallback {
                override fun onProgress(progress: Float, message: String) {
                    _cloneState.value = CloneState.Cloning(progress)
                }

                override fun onError(error: String) {
                    _cloneState.value = CloneState.Error(error)
                }

                override fun onSuccess() {
                    _cloneState.value = CloneState.Success(targetPath)
                }
            })
        }
    }
}
