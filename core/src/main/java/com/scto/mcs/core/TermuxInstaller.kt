package com.scto.mcs.core

import android.system.Os
import android.system.OsConstants
import com.scto.mcs.core.constants.TermuxConstants
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TermuxInstaller @Inject constructor() {

    /**
     * Entpackt ein Termux-Bootstrap-Archiv und korrigiert Symlinks.
     * Da Android keine Symlinks in normalen Verzeichnissen unterstützt,
     * müssen diese nach dem Entpacken manuell via Os.symlink wiederhergestellt werden.
     */
    fun installBootstrap(zipFile: File) {
        val targetDir = File(TermuxConstants.FILES_PATH)
        if (!targetDir.exists()) targetDir.mkdirs()

        ZipInputStream(zipFile.inputStream()).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val newFile = File(targetDir, entry.name)
                
                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    // Prüfen, ob es ein Symlink ist (im Bootstrap oft als Datei mit Pfadinhalt)
                    if (isSymlinkEntry(entry)) {
                        val targetPath = zis.bufferedReader().readLine()
                        try {
                            // Symlink erstellen: targetPath -> newFile
                            // Wir löschen die Platzhalter-Datei zuerst
                            if (newFile.exists()) newFile.delete()
                            Os.symlink(targetPath, newFile.absolutePath)
                        } catch (e: Exception) {
                            // Logging für Debugging
                        }
                    } else {
                        newFile.parentFile?.mkdirs()
                        FileOutputStream(newFile).use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }

    private fun isSymlinkEntry(entry: java.util.zip.ZipEntry): Boolean {
        // Termux-Bootstrap-Archive markieren Symlinks oft durch spezielle Dateiendungen
        // oder wir prüfen den Dateityp, falls das Archiv dies unterstützt.
        return entry.name.endsWith(".symlink")
    }
}
