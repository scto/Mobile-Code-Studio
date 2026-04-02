package com.scto.mcs.feature.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.TermuxInstaller
import com.scto.mcs.core.constants.TermuxConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val termuxInstaller: TermuxInstaller
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _logs = MutableStateFlow("Bereit zur Installation...")
    val logs: StateFlow<String> = _logs

    fun startSetup() {
        viewModelScope.launch {
            try {
                _logs.value = "Starte Download..."
                _progress.value = 0.1f
                
                val zipFile = File(TermuxConstants.TMP, "bootstrap.zip")
                val success = termuxInstaller.downloadBootstrap(zipFile)
                
                if (success) {
                    _logs.value = "Download erfolgreich. Entpacke..."
                    _progress.value = 0.5f
                    
                    termuxInstaller.installBootstrap(zipFile)
                    
                    _logs.value = "Installation abgeschlossen!"
                    _progress.value = 1.0f
                } else {
                    _logs.value = "Fehler beim Download."
                }
            } catch (e: Exception) {
                _logs.value = "Fehler: ${e.message}"
            }
        }
    }
}
