package com.scto.mcs.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.GitCallback
import com.scto.mcs.core.GitManager
import com.scto.mcs.core.TemplateEngine
import com.scto.mcs.core.model.ProjectConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class CloneState {
    object Idle : CloneState()
    data class Cloning(val progress: Float) : CloneState()
    data class Success(val path: String) : CloneState()
    data class Error(val message: String) : CloneState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val gitManager: GitManager,
    private val templateEngine: TemplateEngine
) : ViewModel() {

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

    fun createProject(config: ProjectConfig) {
        viewModelScope.launch(Dispatchers.IO) {
            templateEngine.generateProject(config)
            _cloneState.value = CloneState.Success(config.targetDir)
        }
    }
}
