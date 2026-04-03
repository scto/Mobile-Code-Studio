package com.scto.mcs.core

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BackupRestoreService: Service zum Packen und Wiederherstellen von VCSpace-Containern.
 */
@Singleton
class BackupRestoreService @Inject constructor(
    private val containerManager: ContainerManager
) {

    /**
     * Erstellt ein Backup eines Containers als ZIP-Datei.
     * @param containerId Die ID des zu sichernden Containers.
     * @param outputFile Die Zieldatei für das Backup.
     */
    fun backupContainer(containerId: String, outputFile: File) {
        val containerDir = containerManager.getContainerRoot(containerId)
        
        ZipOutputStream(FileOutputStream(outputFile)).use { zip ->
            containerDir.walkTopDown().forEach { file ->
                if (file.isFile) {
                    val entryName = file.relativeTo(containerDir).path
                    zip.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { input ->
                        input.copyTo(zip)
                    }
                    zip.closeEntry()
                }
            }
        }
    }

    /**
     * Stellt einen Container aus einer ZIP-Datei wieder her.
     * @param containerId Die ID des Zielcontainers.
     * @param backupFile Die Backup-ZIP-Datei.
     */
    fun restoreContainer(containerId: String, backupFile: File) {
        val containerDir = containerManager.createContainer(containerId)
        
        java.util.zip.ZipInputStream(backupFile.inputStream()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val outputFile = File(containerDir, entry.name)
                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.parentFile?.mkdirs()
                    outputFile.outputStream().use { output ->
                        zip.copyTo(output)
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
    }
}
