package com.scto.mcs.termux.session

import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.FileInputStream

/**
 * Verwaltet eine Terminal-Session und die Kommunikation mit dem PTY.
 */
class TerminalSession(
    private val command: String,
    private val workingDirectory: String?,
    private val environment: Array<String>?
) {
    private var mFd: FileDescriptor? = null
    private var mProcessId: Int = 0
    private var mInput: FileOutputStream? = null
    private var mOutput: FileInputStream? = null

    fun start() {
        // Hier würde der native Aufruf zum Starten des PTY erfolgen
        // mFd = Native.createSubprocess(command, workingDirectory, environment)
        // mInput = FileOutputStream(mFd)
        // mOutput = FileInputStream(mFd)
    }

    fun write(data: ByteArray) {
        mInput?.write(data)
        mInput?.flush()
    }

    fun close() {
        mInput?.close()
        mOutput?.close()
    }
}
