package com.scto.mcs.core.setup

import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hält die Konfiguration und Logik für den Termux-Bootstrap-Download bereit.
 * Diese Klasse mappt die Geräte-Architektur (ABI) auf die spezifischen URLs von Visual-Code-Space.
 */
@Singleton
class BootstrapConfig @Inject constructor() {

    companion object {
        private const val BASE_URL = "https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-16.12.2023"
        
        const val URL_AARCH64 = "$BASE_URL/bootstrap-aarch64.zip"
        const val URL_ARM = "$BASE_URL/bootstrap-arm.zip"
        const val URL_X86_64 = "$BASE_URL/bootstrap-x86_64.zip"
    }

    /**
     * Ermittelt die passende Bootstrap-URL basierend auf den unterstützten ABIs des Geräts.
     * Priorisiert 64-Bit-Architekturen.
     * * @return Die URL zum passenden ZIP-Archiv.
     * @throws IllegalStateException wenn keine kompatible Architektur gefunden wurde.
     */
    fun getBootstrapUrl(): String {
        val supportedAbis = Build.SUPPORTED_ABIS
        
        return when {
            supportedAbis.contains("arm64-v8a") -> URL_AARCH64
            supportedAbis.contains("armeabi-v7a") -> URL_ARM
            supportedAbis.contains("x86_64") -> URL_X86_64
            else -> throw IllegalStateException(
                "Inkompatible Architektur: ${supportedAbis.joinToString(", ")}. " +
                "Mobile-Code-Studio unterstützt nur aarch64, arm und x86_64."
            )
        }
    }

    /**
     * Hilfsmethode zur Identifizierung des Architektur-Namens für interne Pfade.
     */
    fun getArchName(): String {
        val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: ""
        return when {
            abi.contains("arm64") -> "aarch64"
            abi.contains("arm") -> "arm"
            abi.contains("x86_64") -> "x86_64"
            else -> "unknown"
        }
    }
}
