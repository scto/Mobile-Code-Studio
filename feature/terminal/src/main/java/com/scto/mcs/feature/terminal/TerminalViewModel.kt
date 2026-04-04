package com.scto.mcs.feature.terminal

import androidx.lifecycle.ViewModel
import com.scto.mcs.feature.terminal.session.TerminalSession
import com.scto.mcs.feature.terminal.session.TerminalSessionListener
import com.scto.mcs.core.termux.shared.TerminalPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val preferences: TerminalPreferences
) : ViewModel(), TerminalSessionListener {

    private var session: TerminalSession? = null

    fun startSession(command: String = "/system/bin/sh") {
        session = TerminalSession(command, null, null, this)
        session?.start()
    }

    override fun onSessionStarted() {
        // Handle session start
    }

    override fun onSessionClosed() {
        // Handle session close
    }

    override fun onOutputReceived(data: ByteArray) {
        // Handle output
    }

    override fun onError(message: String) {
        // Handle error
    }

    override fun onCleared() {
        session?.close()
        super.onCleared()
    }
}
