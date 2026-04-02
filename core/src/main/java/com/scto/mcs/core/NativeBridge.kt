package com.scto.mcs.core

object NativeBridge {

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
