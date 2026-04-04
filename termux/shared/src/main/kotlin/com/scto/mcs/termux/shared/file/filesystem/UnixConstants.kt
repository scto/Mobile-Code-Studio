package com.scto.mcs.termux.shared.file.filesystem

import android.system.OsConstants

object UnixConstants {
    val O_RDONLY = OsConstants.O_RDONLY
    val O_WRONLY = OsConstants.O_WRONLY
    val O_RDWR = OsConstants.O_RDWR
    val O_APPEND = OsConstants.O_APPEND
    val O_CREAT = OsConstants.O_CREAT
    val O_EXCL = OsConstants.O_EXCL
    val O_TRUNC = OsConstants.O_TRUNC
    val O_SYNC = OsConstants.O_SYNC
    val O_NOFOLLOW = OsConstants.O_NOFOLLOW

    val S_IAMB = (OsConstants.S_IRUSR or OsConstants.S_IWUSR or OsConstants.S_IXUSR or
            OsConstants.S_IRGRP or OsConstants.S_IWGRP or OsConstants.S_IXGRP or
            OsConstants.S_IROTH or OsConstants.S_IWOTH or OsConstants.S_IXOTH)

    val S_IRUSR = OsConstants.S_IRUSR
    val S_IWUSR = OsConstants.S_IWUSR
    val S_IXUSR = OsConstants.S_IXUSR
    val S_IRGRP = OsConstants.S_IRGRP
    val S_IWGRP = OsConstants.S_IWGRP
    val S_IXGRP = OsConstants.S_IXGRP
    val S_IROTH = OsConstants.S_IROTH
    val S_IWOTH = OsConstants.S_IWOTH
    val S_IXOTH = OsConstants.S_IXOTH
    val S_IFMT = OsConstants.S_IFMT
    val S_IFREG = OsConstants.S_IFREG
    val S_IFDIR = OsConstants.S_IFDIR
    val S_IFLNK = OsConstants.S_IFLNK
    val S_IFSOCK = OsConstants.S_IFSOCK
    val S_IFCHR = OsConstants.S_IFCHR
    val S_IFBLK = OsConstants.S_IFBLK
    val S_IFIFO = OsConstants.S_IFIFO
    val R_OK = OsConstants.R_OK
    val W_OK = OsConstants.W_OK
    val X_OK = OsConstants.X_OK
    val F_OK = OsConstants.F_OK
    val ENOENT = OsConstants.ENOENT
    val EACCES = OsConstants.EACCES
    val EEXIST = OsConstants.EEXIST
    val ENOTDIR = OsConstants.ENOTDIR
    val EINVAL = OsConstants.EINVAL
    val EXDEV = OsConstants.EXDEV
    val EISDIR = OsConstants.EISDIR
    val ENOTEMPTY = OsConstants.ENOTEMPTY
    val ENOSPC = OsConstants.ENOSPC
    val EAGAIN = OsConstants.EAGAIN
    val ENOSYS = OsConstants.ENOSYS
    val ELOOP = OsConstants.ELOOP
    val EROFS = OsConstants.EROFS
    val ENODATA = OsConstants.ENODATA
    val ERANGE = OsConstants.ERANGE
    val EMFILE = OsConstants.EMFILE

    const val AT_SYMLINK_NOFOLLOW = 0x100
    const val AT_REMOVEDIR = 0x200
}
