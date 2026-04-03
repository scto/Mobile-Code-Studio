package com.scto.mcs.feature.terminal

import androidx.lifecycle.ViewModel
import com.scto.mcs.termux.session.TerminalSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor() : ViewModel() {

    private var session: TerminalSession? = null

    fun startSession(command: String = "/system/bin/sh") {
        session = TerminalSession(command, null, null)
        session?.start()
    }

    override fun onCleared() {
        session?.close()
        super.onCleared()
    }
}
