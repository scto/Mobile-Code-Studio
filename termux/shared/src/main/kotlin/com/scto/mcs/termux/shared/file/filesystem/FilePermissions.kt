package com.scto.mcs.termux.shared.file.filesystem

import com.termux.shared.file.filesystem.FilePermission.*
import java.util.EnumSet

object FilePermissions {

    private fun writeBits(sb: StringBuilder, r: Boolean, w: Boolean, x: Boolean) {
        sb.append(if (r) 'r' else '-')
        sb.append(if (w) 'w' else '-')
        sb.append(if (x) 'x' else '-')
    }

    fun toString(perms: Set<FilePermission>): String {
        val sb = StringBuilder(9)
        writeBits(sb, perms.contains(OWNER_READ), perms.contains(OWNER_WRITE), perms.contains(OWNER_EXECUTE))
        writeBits(sb, perms.contains(GROUP_READ), perms.contains(GROUP_WRITE), perms.contains(GROUP_EXECUTE))
        writeBits(sb, perms.contains(OTHERS_READ), perms.contains(OTHERS_WRITE), perms.contains(OTHERS_EXECUTE))
        return sb.toString()
    }

    private fun isSet(c: Char, setValue: Char): Boolean {
        if (c == setValue) return true
        if (c == '-') return false
        throw IllegalArgumentException("Invalid mode")
    }

    private fun isR(c: Char) = isSet(c, 'r')
    private fun isW(c: Char) = isSet(c, 'w')
    private fun isX(c: Char) = isSet(c, 'x')

    fun fromString(perms: String): Set<FilePermission> {
        if (perms.length != 9) throw IllegalArgumentException("Invalid mode")
        val result = EnumSet.noneOf(FilePermission::class.java)
        if (isR(perms[0])) result.add(OWNER_READ)
        if (isW(perms[1])) result.add(OWNER_WRITE)
        if (isX(perms[2])) result.add(OWNER_EXECUTE)
        if (isR(perms[3])) result.add(GROUP_READ)
        if (isW(perms[4])) result.add(GROUP_WRITE)
        if (isX(perms[5])) result.add(GROUP_EXECUTE)
        if (isR(perms[6])) result.add(OTHERS_READ)
        if (isW(perms[7])) result.add(OTHERS_WRITE)
        if (isX(perms[8])) result.add(OTHERS_EXECUTE)
        return result
    }
}
