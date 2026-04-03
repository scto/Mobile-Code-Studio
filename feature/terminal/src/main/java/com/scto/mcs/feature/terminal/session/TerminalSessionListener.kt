package com.scto.mcs.feature.terminal.session

/**
 * Interface für Rückrufe aus der Terminal-Session.
 */
interface TerminalSessionListener {
    fun onSessionStarted()
    fun onSessionClosed()
    fun onOutputReceived(data: ByteArray)
    fun onError(message: String)
}
