package com.scto.mcs.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeBridge @Inject constructor() {

    init {
        System.loadLibrary("vcspace")
    }

    external fun stringFromJNI(): String

    private external fun nativeCreateSymlink(target: String, linkpath: String): Int
    private external fun nativeCheckSymlinkSupport(testDir: String): Boolean
    private external fun nativeCheckFileSystemCapabilities(testDir: String): Int

    data class FileSystemCapabilities(
        val supportsSymlinks: Boolean,
        val isWritable: Boolean
    )

    /**
     * Erstellt einen symbolischen Link.
     * @param target Der Pfad, auf den der Link zeigen soll.
     * @param linkpath Der Pfad des zu erstellenden Links.
     * @return Result<Unit> bei Erfolg, sonst Failure.
     */
    fun createSymlink(target: String, linkpath: String): Result<Unit> {
        val result = nativeCreateSymlink(target, linkpath)
        return if (result == 0) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to create symlink: $target -> $linkpath (error code: $result)"))
        }
    }

    /**
     * Prüft, ob das Dateisystem Symlinks unterstützt.
     * @param testDir Ein Verzeichnis, in dem der Test durchgeführt werden soll.
     * @return true, wenn Symlinks unterstützt werden.
     */
    fun checkSymlinkSupport(testDir: String): Boolean {
        return nativeCheckSymlinkSupport(testDir)
    }

    /**
     * Führt einen umfassenden Test der Dateisystem-Fähigkeiten durch.
     * @param testDir Ein Verzeichnis, in dem der Test durchgeführt werden soll.
     * @return FileSystemCapabilities Objekt mit den Testergebnissen.
     */
    fun checkFileSystemCapabilities(testDir: String): FileSystemCapabilities {
        val result = nativeCheckFileSystemCapabilities(testDir)
        // Bit 0: Symlinks, Bit 1: Writable
        return FileSystemCapabilities(
            supportsSymlinks = (result and 1) != 0,
            isWritable = (result and 2) != 0
        )
    }
}
