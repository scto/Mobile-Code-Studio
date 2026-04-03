package com.scto.mcs.feature.settings

import androidx.lifecycle.ViewModel
import com.scto.mcs.core.constants.TermuxConstants
import com.scto.mcs.termux.shared.TerminalPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val terminalPreferences: TerminalPreferences
) : ViewModel() {

    val fontSize: Int get() = terminalPreferences.fontSize
    val colorScheme: String get() = terminalPreferences.colorScheme

    fun setFontSize(size: Int) {
        terminalPreferences.fontSize = size
    }

    fun setColorScheme(scheme: String) {
        terminalPreferences.colorScheme = scheme
    }

    fun resetEnvironment() {
        val prefixDir = File(TermuxConstants.PREFIX)
        if (prefixDir.exists()) {
            prefixDir.deleteRecursively()
        }
        // Hier könnte ein Trigger für den Setup-Prozess folgen
    }
}
