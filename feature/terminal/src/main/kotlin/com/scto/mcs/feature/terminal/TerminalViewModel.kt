package com.scto.mcs.feature.terminal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor() : ViewModel() {
    // Hier wird später die TerminalSession verwaltet
    // In einer echten Implementierung würde hier die Session injiziert oder erstellt werden
}
