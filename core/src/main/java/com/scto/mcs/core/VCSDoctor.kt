package com.scto.mcs.core

import java.io.File
import java.nio.file.Files
import javax.inject.Inject
import javax.inject.Singleton

/**
 * VCS-Doctor: Diagnose-Tool zum Scannen und Reparieren von Dateisystem-Problemen.
 */
@Singleton
class VCSDoctor @Inject constructor() {

    data class ScanResult(
        val deadLinks: List<File>,
        val totalScanned: Int
    )

    /**
     * Scannt ein Verzeichnis nach toten symbolischen Links.
     * @param rootDir Das zu scannende Verzeichnis.
     * @return ScanResult mit den gefundenen toten Links.
     */
    fun scanForDeadLinks(rootDir: File): ScanResult {
        val deadLinks = mutableListOf<File>()
        var totalScanned = 0

        if (!rootDir.exists() || !rootDir.isDirectory) {
            return ScanResult(emptyList(), 0)
        }

        rootDir.walkTopDown().forEach { file ->
            if (Files.isSymbolicLink(file.toPath())) {
                totalScanned++
                // Prüfen, ob das Ziel existiert
                if (!file.exists()) {
                    deadLinks.add(file)
                }
            }
        }

        return ScanResult(deadLinks, totalScanned)
    }

    /**
     * Repariert (löscht) einen toten symbolischen Link.
     * @param linkFile Die Datei, die gelöscht werden soll.
     * @return true bei Erfolg.
     */
    fun repairDeadLink(linkFile: File): Boolean {
        return if (Files.isSymbolicLink(linkFile.toPath()) && !linkFile.exists()) {
            linkFile.delete()
        } else {
            false
        }
    }
}
