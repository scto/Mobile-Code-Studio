package com.scto.mcs.termux.application

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Zentrale Konfiguration für die Termux-Umgebung.
 */
@Singleton
class TermuxSettings @Inject constructor() {
    val isEnvironmentInitialized: Boolean
        get() = true // Hier würde die Prüfung gegen das Dateisystem erfolgen
}
