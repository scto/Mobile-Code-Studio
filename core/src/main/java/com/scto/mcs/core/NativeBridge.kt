package com.scto.mcs.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeBridge @Inject constructor() {

    init {
        System.loadLibrary("vcspace")
    }

    external fun stringFromJNI(): String

    /**
     * Erstellt einen symbolischen Link.
     * @param target Der Pfad, auf den der Link zeigen soll.
     * @param linkpath Der Pfad des zu erstellenden Links.
     * @return 0 bei Erfolg, sonst Fehlercode.
     */
    external fun createSymlink(target: String, linkpath: String): Int
}
