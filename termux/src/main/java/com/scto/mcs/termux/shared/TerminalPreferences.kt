package com.scto.mcs.termux.shared

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Verwaltet Terminal-spezifische Einstellungen.
 */
@Singleton
class TerminalPreferences @Inject constructor() {
    var fontSize: Int = 14
    var colorScheme: String = "monokai"
    var fontFamily: String = "monospace"
}
