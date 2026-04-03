package com.scto.mcs.termux.shared.file.filesystem

import android.system.OsConstants

object UnixConstants {
    const val O_RDONLY = OsConstants.O_RDONLY
    const val O_WRONLY = OsConstants.O_WRONLY
    const val O_RDWR = OsConstants.O_RDWR
    const val O_APPEND = OsConstants.O_APPEND
    const val O_CREAT = OsConstants.O_CREAT
    const val O_EXCL = OsConstants.O_EXCL
    const val O_TRUNC = OsConstants.O_TRUNC
    const val O_SYNC = OsConstants.O_SYNC
    const val O_NOFOLLOW = OsConstants.O_NOFOLLOW

    val S_IAMB = (OsConstants.S_IRUSR or OsConstants.S_IWUSR or OsConstants.S_IXUSR or
            OsConstants.S_IRGRP or OsConstants.S_IWGRP or OsConstants.S_IXGRP or
            OsConstants.S_IROTH or OsConstants.S_IWOTH or OsConstants.S_IXOTH)

    const val S_IRUSR = OsConstants.S_IRUSR
    const val S_IWUSR = OsConstants.S_IWUSR
    const val S_IXUSR = OsConstants.S_IXUSR
    const val S_IRGRP = OsConstants.S_IRGRP
    const val S_IWGRP = OsConstants.S_IWGRP
    const val S_IXGRP = OsConstants.S_IXGRP
    const val S_IROTH = OsConstants.S_IROTH
    const val S_IWOTH = OsConstants.S_IWOTH
    const val S_IXOTH = OsConstants.S_IXOTH
    const val S_IFMT = OsConstants.S_IFMT
    const val S_IFREG = OsConstants.S_IFREG
    const val S_IFDIR = OsConstants.S_IFDIR
    const val S_IFLNK = OsConstants.S_IFLNK
    const val S_IFSOCK = OsConstants.S_IFSOCK
    const val S_IFCHR = OsConstants.S_IFCHR
    const val S_IFBLK = OsConstants.S_IFBLK
    const val S_IFIFO = OsConstants.S_IFIFO
    const val R_OK = OsConstants.R_OK
    const val W_OK = OsConstants.W_OK
    const val X_OK = OsConstants.X_OK
    const val F_OK = OsConstants.F_OK
    const val ENOENT = OsConstants.ENOENT
    const val EACCES = OsConstants.EACCES
    const val EEXIST = OsConstants.EEXIST
    const val ENOTDIR = OsConstants.ENOTDIR
    const val EINVAL = OsConstants.EINVAL
    const val EXDEV = OsConstants.EXDEV
    const val EISDIR = OsConstants.EISDIR
    const val ENOTEMPTY = OsConstants.ENOTEMPTY
    const val ENOSPC = OsConstants.ENOSPC
    const val EAGAIN = OsConstants.EAGAIN
    const val ENOSYS = OsConstants.ENOSYS
    const val ELOOP = OsConstants.ELOOP
    const val EROFS = OsConstants.EROFS
    const val ENODATA = OsConstants.ENODATA
    const val ERANGE = OsConstants.ERANGE
    const val EMFILE = OsConstants.EMFILE

    const val AT_SYMLINK_NOFOLLOW = 0x100
    const val AT_REMOVEDIR = 0x200
}
