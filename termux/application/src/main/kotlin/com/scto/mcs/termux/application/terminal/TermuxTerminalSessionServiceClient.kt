package com.scto.mcs.termux.application.terminal

import com.termux.app.TermuxService
import com.scto.mcs.termux.shared.termux.shell.command.runner.terminal.TermuxSession
import com.scto.mcs.termux.shared.termux.terminal.TermuxTerminalSessionClientBase
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import java.io.Closeable

/** The [TerminalSessionClient] implementation that may require a [android.app.Service] for its interface methods. */
class TermuxTerminalSessionServiceClient(private var service: TermuxService?) : TermuxTerminalSessionClientBase(), Closeable {

    companion object {
        private const val LOG_TAG = "TermuxTerminalSessionServiceClient"
    }

    override fun setTerminalShellPid(terminalSession: TerminalSession, pid: Int) {
        val termuxSession = service?.getTermuxSessionForTerminalSession(terminalSession)
        if (termuxSession != null)
            termuxSession.executionCommand.mPid = pid
    }

    override fun close() {
        service = null
    }
}
