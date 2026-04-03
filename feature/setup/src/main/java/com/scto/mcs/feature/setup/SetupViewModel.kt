package com.scto.mcs.feature.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.TermuxInstaller
import com.scto.mcs.core.constants.TermuxConstants
import com.scto.mcs.core.ui.components.InstallPhase
import com.scto.mcs.core.ui.components.ProgressState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val termuxInstaller: TermuxInstaller
) : ViewModel() {

    private val _jdkVersion = MutableStateFlow("openjdk-21")
    val jdkVersion: StateFlow<String> = _jdkVersion

    private val _buildToolsVersion = MutableStateFlow("35")
    val buildToolsVersion: StateFlow<String> = _buildToolsVersion

    private val _progressState = MutableStateFlow(ProgressState(InstallPhase.IDLE, 0f, "Bereit zur Installation"))
    val progressState: StateFlow<ProgressState> = _progressState

    fun setJdkVersion(version: String) { _jdkVersion.value = version }
    fun setBuildToolsVersion(version: String) { _buildToolsVersion.value = version }

    fun startInstallation() {
        viewModelScope.launch {
            _progressState.value = ProgressState(InstallPhase.DOWNLOAD, 0.1f, "Lade Bootstrap herunter...")
            
            val tempFile = File(TermuxConstants.TMP, "bootstrap.zip")
            val success = termuxInstaller.downloadBootstrap(tempFile)
            
            if (success) {
                _progressState.value = ProgressState(InstallPhase.EXTRACTION, 0.5f, "Entpacke Dateien...")
                termuxInstaller.installBootstrap(tempFile)
                
                _progressState.value = ProgressState(InstallPhase.COMPLETED, 1.0f, "Installation abgeschlossen!")
            } else {
                _progressState.value = ProgressState(InstallPhase.IDLE, 0f, "Fehler beim Download")
            }
        }
    }
}
