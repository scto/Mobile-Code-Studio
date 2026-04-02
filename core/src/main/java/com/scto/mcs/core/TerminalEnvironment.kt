package com.scto.mcs.core

import com.scto.mcs.core.constants.TermuxConstants
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalEnvironment @Inject constructor() {

    init {
        initFolders()
    }

    private fun initFolders() {
        File(TermuxConstants.PREFIX).mkdirs()
        File(TermuxConstants.HOME).mkdirs()
        File(TermuxConstants.BIN).mkdirs()
        File(TermuxConstants.TMP).mkdirs()
    }

    fun getEnv(): Map<String, String> {
        return mapOf(
            "HOME" to TermuxConstants.HOME,
            "PATH" to "${TermuxConstants.BIN}:/system/bin",
            "LD_LIBRARY_PATH" to "${TermuxConstants.PREFIX}/lib",
            "JAVA_HOME" to "/system/lib/jvm",
            "TERM" to "xterm-256color"
        )
    }
}
