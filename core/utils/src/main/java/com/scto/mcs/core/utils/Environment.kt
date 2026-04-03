package com.scto.mcs.core.utils

import com.scto.mcs.core.constants.TermuxConstants
import java.io.File

object Environment {
    fun getEnv(): Map<String, String> {
        return mapOf(
            "HOME" to TermuxConstants.HOME,
            "PATH" to "${TermuxConstants.BIN}:/system/bin",
            "LD_LIBRARY_PATH" to "${TermuxConstants.PREFIX}/lib",
            "TERM" to "xterm-256color",
            "PREFIX" to TermuxConstants.PREFIX,
            "TMPDIR" to TermuxConstants.TMP
        )
    }

    fun ensureDirectories() {
        File(TermuxConstants.PREFIX).mkdirs()
        File(TermuxConstants.HOME).mkdirs()
        File(TermuxConstants.BIN).mkdirs()
        File(TermuxConstants.TMP).mkdirs()
    }
}
