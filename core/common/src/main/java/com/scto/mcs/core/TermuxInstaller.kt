package com.scto.mcs.core

import android.util.Log
import com.scto.mcs.core.constants.TermuxConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.UUID
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TermuxInstaller @Inject constructor(
    private val bootstrapConfig: BootstrapConfig,
    private val nativeBridge: NativeBridge
) {

    private val TAG = "TermuxInstaller"

    /**
     * Lädt das Bootstrap-Archiv herunter.
     */
    suspend fun downloadBootstrap(destination: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL(bootstrapConfig.getBootstrapUrl())
            url.openStream().use { input ->
                FileOutputStream(destination).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Download failed", e)
            false
        }
    }

    /**
     * Entpackt ein Termux-Bootstrap-Archiv, korrigiert Symlinks via NativeBridge
     * und setzt die korrekten Berechtigungen für Binärdateien.
     */
    suspend fun installBootstrap(zipFile: File) = withContext(Dispatchers.IO) {
        val targetDir = File(TermuxConstants.FILES_PATH)
        if (!targetDir.exists()) targetDir.mkdirs()

        ZipInputStream(zipFile.inputStream()).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val newFile = File(targetDir, entry.name)
                
                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()

                    if (isSymlinkEntry(entry)) {
                        // Symlink-Ziel aus dem Dateiinhalt lesen
                        val targetPath = zis.bufferedReader().readLine()?.trim()
                        if (!targetPath.isNullOrEmpty()) {
                            // Atomare Symlink-Erstellung:
                            // 1. Symlink in TMP erstellen
                            // 2. Atomar verschieben
                            val tempSymlink = File(TermuxConstants.TMP, UUID.randomUUID().toString())
                            
                            val result = nativeBridge.createSymlink(targetPath, tempSymlink.absolutePath)
                            result.onSuccess {
                                if (newFile.exists()) newFile.delete()
                                if (!tempSymlink.renameTo(newFile)) {
                                    Log.e(TAG, "Failed to move symlink: ${tempSymlink.absolutePath} -> ${newFile.absolutePath}")
                                }
                            }.onFailure { e ->
                                Log.e(TAG, "Failed to create symlink: ${newFile.absolutePath} -> $targetPath", e)
                            }
                        }
                    } else {
                        // Normale Datei entpacken
                        FileOutputStream(newFile).use { fos ->
                            zis.copyTo(fos)
                        }
                        
                        // Berechtigungen setzen: chmod 700 für Binaries
                        if (newFile.absolutePath.startsWith(TermuxConstants.BIN)) {
                            newFile.setExecutable(true, true) // ownerOnly = true
                            newFile.setReadable(true, true)
                            newFile.setWritable(true, true)
                        }
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }

    private fun isSymlinkEntry(entry: java.util.zip.ZipEntry): Boolean {
        return entry.name.endsWith(".symlink")
    }
}
