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
}
