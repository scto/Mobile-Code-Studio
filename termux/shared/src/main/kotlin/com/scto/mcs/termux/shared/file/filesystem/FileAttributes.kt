package com.scto.mcs.termux.shared.file.filesystem

import android.os.Build
import android.system.StructStat
import com.termux.shared.logger.Logger
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.util.EnumSet
import java.util.HashSet
import java.util.concurrent.TimeUnit

class FileAttributes private constructor(
    private val filePath: String?,
    private val fileDescriptor: FileDescriptor? = null
) {

    private var st_mode: Int = 0
    private var st_ino: Long = 0
    private var st_dev: Long = 0
    private var st_rdev: Long = 0
    private var st_nlink: Long = 0
    private var st_uid: Int = 0
    private var st_gid: Int = 0
    private var st_size: Long = 0
    private var st_blksize: Long = 0
    private var st_blocks: Long = 0
    private var st_atime_sec: Long = 0
    private var st_atime_nsec: Long = 0
    private var st_mtime_sec: Long = 0
    private var st_mtime_nsec: Long = 0
    private var st_ctime_sec: Long = 0
    private var st_ctime_nsec: Long = 0

    @Volatile private var owner: String? = null
    @Volatile private var group: String? = null
    @Volatile private var key: FileKey? = null

    companion object {
        @Throws(IOException::class)
        fun get(filePath: String?, followLinks: Boolean): FileAttributes {
            val fileAttributes = if (filePath.isNullOrEmpty()) {
                FileAttributes(null)
            } else {
                FileAttributes(File(filePath).absolutePath)
            }

            if (followLinks) {
                NativeDispatcher.stat(filePath, fileAttributes)
            } else {
                NativeDispatcher.lstat(filePath, fileAttributes)
            }
            return fileAttributes
        }

        @Throws(IOException::class)
        fun get(fileDescriptor: FileDescriptor): FileAttributes {
            val fileAttributes = FileAttributes(fileDescriptor)
            NativeDispatcher.fstat(fileDescriptor, fileAttributes)
            return fileAttributes
        }

        private fun toFileTime(sec: Long, nsec: Long): FileTime {
            return if (nsec == 0L) {
                FileTime.from(sec, TimeUnit.SECONDS)
            } else {
                val micro = sec * 1000000L + nsec / 1000L
                FileTime.from(micro, TimeUnit.MICROSECONDS)
            }
        }

        fun getFileAttributesLogString(fileAttributes: FileAttributes?): String {
            if (fileAttributes == null) return "null"

            val logString = StringBuilder()
            logString.append(fileAttributes.getFileString())
            logString.append("\n").append(fileAttributes.getTypeString())
            logString.append("\n").append(fileAttributes.getSizeString())
            logString.append("\n").append(fileAttributes.getBlocksString())
            logString.append("\n").append(fileAttributes.getIOBlockString())
            logString.append("\n").append(fileAttributes.getDeviceString())
            logString.append("\n").append(fileAttributes.getInodeString())
            logString.append("\n").append(fileAttributes.getLinksString())

            if (fileAttributes.isBlock() || fileAttributes.isCharacter())
                logString.append("\n").append(fileAttributes.getDeviceTypeString())

            logString.append("\n").append(fileAttributes.getOwnerString())
            logString.append("\n").append(fileAttributes.getGroupString())
            logString.append("\n").append(fileAttributes.getPermissionString())
            logString.append("\n").append(fileAttributes.getAccessTimeString())
            logString.append("\n").append(fileAttributes.getModifiedTimeString())
            logString.append("\n").append(fileAttributes.getChangeTimeString())

            return logString.toString()
        }
    }

    fun file(): String? = filePath ?: fileDescriptor?.toString()

    fun isSameFile(attrs: FileAttributes): Boolean = (st_ino == attrs.st_ino) && (st_dev == attrs.st_dev)

    fun mode(): Int = st_mode
    fun blksize(): Long = st_blksize
    fun blocks(): Long = st_blocks
    fun ino(): Long = st_ino
    fun dev(): Long = st_dev
    fun rdev(): Long = st_rdev
    fun nlink(): Long = st_nlink
    fun uid(): Int = st_uid
    fun gid(): Int = st_gid
    fun size(): Long = st_size

    fun lastAccessTime(): FileTime = toFileTime(st_atime_sec, st_atime_nsec)
    fun lastModifiedTime(): FileTime = toFileTime(st_mtime_sec, st_mtime_nsec)
    fun lastChangeTime(): FileTime = toFileTime(st_ctime_sec, st_ctime_nsec)
    fun creationTime(): FileTime = lastModifiedTime()

    fun isRegularFile(): Boolean = (st_mode and UnixConstants.S_IFMT) == UnixConstants.S_IFREG
    fun isDirectory(): Boolean = (st_mode and UnixConstants.S_IFMT) == UnixConstants.S_IFDIR
    fun isSymbolicLink(): Boolean = (st_mode and UnixConstants.S_IFMT) == UnixConstants.S_IFLNK
    fun isCharacter(): Boolean = (st_mode and UnixConstants.S_IFMT) == UnixConstants.S_IFCHR
    fun isFifo(): Boolean = (st_mode and UnixConstants.S_IFMT) == UnixConstants.S_IFIFO
    fun isSocket(): Boolean = (st_mode and UnixConstants.S_IFMT) == UnixConstants.S_IFSOCK
    fun isBlock(): Boolean = (st_mode and UnixConstants.S_IFMT) == UnixConstants.S_IFBLK

    fun isOther(): Boolean {
        val type = st_mode and UnixConstants.S_IFMT
        return (type != UnixConstants.S_IFREG && type != UnixConstants.S_IFDIR && type != UnixConstants.S_IFLNK)
    }

    fun isDevice(): Boolean {
        val type = st_mode and UnixConstants.S_IFMT
        return (type == UnixConstants.S_IFCHR || type == UnixConstants.S_IFBLK || type == UnixConstants.S_IFIFO)
    }

    fun fileKey(): FileKey {
        if (key == null) {
            synchronized(this) {
                if (key == null) {
                    key = FileKey(st_dev, st_ino)
                }
            }
        }
        return key!!
    }

    fun owner(): String {
        if (owner == null) {
            synchronized(this) {
                if (owner == null) {
                    owner = st_uid.toString()
                }
            }
        }
        return owner!!
    }

    fun group(): String {
        if (group == null) {
            synchronized(this) {
                if (group == null) {
                    group = st_gid.toString()
                }
            }
        }
        return group!!
    }

    fun permissions(): Set<FilePermission> {
        val bits = st_mode and UnixConstants.S_IAMB
        val perms = HashSet<FilePermission>()

        if ((bits and UnixConstants.S_IRUSR) > 0) perms.add(FilePermission.OWNER_READ)
        if ((bits and UnixConstants.S_IWUSR) > 0) perms.add(FilePermission.OWNER_WRITE)
        if ((bits and UnixConstants.S_IXUSR) > 0) perms.add(FilePermission.OWNER_EXECUTE)

        if ((bits and UnixConstants.S_IRGRP) > 0) perms.add(FilePermission.GROUP_READ)
        if ((bits and UnixConstants.S_IWGRP) > 0) perms.add(FilePermission.GROUP_WRITE)
        if ((bits and UnixConstants.S_IXGRP) > 0) perms.add(FilePermission.GROUP_EXECUTE)

        if ((bits and UnixConstants.S_IROTH) > 0) perms.add(FilePermission.OTHERS_READ)
        if ((bits and UnixConstants.S_IWOTH) > 0) perms.add(FilePermission.OTHERS_WRITE)
        if ((bits and UnixConstants.S_IXOTH) > 0) perms.add(FilePermission.OTHERS_EXECUTE)

        return perms
    }

    fun loadFromStructStat(structStat: StructStat) {
        this.st_mode = structStat.st_mode
        this.st_ino = structStat.st_ino.toInt()
        this.st_dev = structStat.st_dev.toInt()
        this.st_rdev = structStat.st_rdev.toInt()
        this.st_nlink = structStat.st_nlink.toInt()
        this.st_uid = structStat.st_uid
        this.st_gid = structStat.st_gid
        this.st_size = structStat.st_size
        this.st_blksize = structStat.st_blksize.toLong()
        this.st_blocks = structStat.st_blocks.toLong()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            this.st_atime_sec = structStat.st_atim.tv_sec
            this.st_atime_nsec = structStat.st_atim.tv_nsec
            this.st_mtime_sec = structStat.st_mtim.tv_sec
            this.st_mtime_nsec = structStat.st_mtim.tv_nsec
            this.st_ctime_sec = structStat.st_ctim.tv_sec
            this.st_ctime_nsec = structStat.st_ctim.tv_nsec
        } else {
            this.st_atime_sec = structStat.st_atime
            this.st_atime_nsec = 0
            this.st_mtime_sec = structStat.st_mtime
            this.st_mtime_nsec = 0
            this.st_ctime_sec = structStat.st_ctime
            this.st_ctime_nsec = 0
        }
    }

    fun getFileString() = "File: `${file()}`"
    fun getTypeString() = "Type: `${FileTypes.getFileType(this).name}`"
    fun getSizeString() = "Size: `${size()}`"
    fun getBlocksString() = "Blocks: `${blocks()}`"
    fun getIOBlockString() = "IO Block: `${blksize()}`"
    fun getDeviceString() = "Device: `${java.lang.Long.toHexString(st_dev)}`"
    fun getInodeString() = "Inode: `$st_ino`"
    fun getLinksString() = "Links: `${nlink()}`"
    fun getDeviceTypeString() = "Device Type: `${rdev()}`"
    fun getOwnerString() = "Owner: `${owner()}`"
    fun getGroupString() = "Group: `${group()}`"
    fun getPermissionString() = "Permissions: `${FilePermissions.toString(permissions())}`"
    fun getAccessTimeString() = "Access Time: `${lastAccessTime()}`"
    fun getModifiedTimeString() = "Modified Time: `${lastModifiedTime()}`"
    fun getChangeTimeString() = "Change Time: `${lastChangeTime()}`"

    override fun toString(): String = getFileAttributesLogString(this)
}
