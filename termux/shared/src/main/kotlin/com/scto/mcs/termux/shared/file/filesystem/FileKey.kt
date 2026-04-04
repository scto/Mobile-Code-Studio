package com.scto.mcs.termux.shared.file.filesystem

class FileKey(private val st_dev: Long, private val st_ino: Long) {

    override fun hashCode(): Int {
        return (st_dev xor (st_dev ushr 32)).toInt() + (st_ino xor (st_ino ushr 32)).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FileKey) return false
        return (this.st_dev == other.st_dev) && (this.st_ino == other.st_ino)
    }

    override fun toString(): String {
        return "(dev=${java.lang.Long.toHexString(st_dev)},ino=$st_ino)"
    }
}
