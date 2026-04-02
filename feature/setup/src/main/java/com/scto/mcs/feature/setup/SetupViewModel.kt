package com.scto.mcs.feature.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.TerminalEnvironment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val terminalEnvironment: TerminalEnvironment
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _logs = MutableStateFlow("Bereit zur Installation...")
    val logs: StateFlow<String> = _logs

    fun startSetup(jdkVersion: String, sdkVersion: String) {
        viewModelScope.launch {
            _logs.value = "Initialisiere Terminal..."
            _progress.value = 0.1f
            delay(1000)
            
            _logs.value = "Installiere JDK $jdkVersion..."
            _progress.value = 0.4f
            delay(1500)
            
            _logs.value = "Installiere Android SDK $sdkVersion..."
            _progress.value = 0.7f
            delay(1500)
            
            _logs.value = "Setup abgeschlossen!"
            _progress.value = 1.0f
        }
    }
}
