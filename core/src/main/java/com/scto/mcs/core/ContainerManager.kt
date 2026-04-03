package com.scto.mcs.core

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ContainerManager: Verwaltet isolierte VCSpace-Instanzen.
 * Ermöglicht das Erstellen und Wechseln zwischen verschiedenen Umgebungen.
 */
@Singleton
class ContainerManager @Inject constructor(
    private val fileSystemUtils: FileSystemUtils
) {

    private var activeContainerId: String = "default"

    /**
     * Gibt das Root-Verzeichnis für eine bestimmte Container-ID zurück.
     */
    fun getContainerRoot(containerId: String): File {
        return fileSystemUtils.getFile("containers/$containerId")
    }

    /**
     * Initialisiert einen neuen Container.
     */
    fun createContainer(containerId: String): File {
        val containerDir = getContainerRoot(containerId)
        if (!containerDir.exists()) {
            containerDir.mkdirs()
        }
        return containerDir
    }

    /**
     * Setzt den aktiven Container.
     */
    fun setActiveContainer(containerId: String) {
        activeContainerId = containerId
    }

    /**
     * Gibt die ID des aktuell aktiven Containers zurück.
     */
    fun getActiveContainerId(): String = activeContainerId

    /**
     * Listet alle verfügbaren Container auf.
     */
    fun listContainers(): List<String> {
        val containersDir = fileSystemUtils.getFile("containers")
        return containersDir.listFiles { file -> file.isDirectory }?.map { it.name } ?: emptyList()
    }
}
