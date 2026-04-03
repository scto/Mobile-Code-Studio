package com.scto.mcs.core.constants

object TermuxConstants {
    const val FILES_PATH = "/data/data/com.scto.mcs/files"
    const val PREFIX = "$FILES_PATH/usr"
    const val HOME = "$FILES_PATH/home"
    const val TMP = "$PREFIX/tmp"
    const val BIN = "$PREFIX/bin"

    const val BOOTSTRAP_URL_AARCH64 = "https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-16.12.2023/bootstrap-aarch64.zip"
    const val BOOTSTRAP_URL_ARM = "https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-16.12.2023/bootstrap-arm.zip"
    const val BOOTSTRAP_URL_X86_64 = "https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-x86_64.zip"
}
