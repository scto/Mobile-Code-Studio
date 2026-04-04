package com.scto.mcs.termux.shared.shell.command.result

import androidx.annotation.NonNull
import com.scto.mcs.termux.shared.data.DataUtils
import com.scto.mcs.termux.shared.errors.Errno
import com.scto.mcs.termux.shared.errors.Error
import com.scto.mcs.termux.shared.logger.Logger
import com.scto.mcs.termux.shared.markdown.MarkdownUtils
import java.io.Serializable
import java.util.ArrayList
import java.util.Collections

class ResultData : Serializable {

    val stdout = StringBuilder()
    val stderr = StringBuilder()
    var exitCode: Int? = null
    var errorsList: MutableList<Error> = ArrayList()

    fun clearStdout() {
        stdout.setLength(0)
    }

    fun prependStdout(message: String): StringBuilder = stdout.insert(0, message)
    fun prependStdoutLn(message: String): StringBuilder = stdout.insert(0, "$message\n")
    fun appendStdout(message: String): StringBuilder = stdout.append(message)
    fun appendStdoutLn(message: String): StringBuilder = stdout.append(message).append("\n")

    fun clearStderr() {
        stderr.setLength(0)
    }

    fun prependStderr(message: String): StringBuilder = stderr.insert(0, message)
    fun prependStderrLn(message: String): StringBuilder = stderr.insert(0, "$message\n")
    fun appendStderr(message: String): StringBuilder = stderr.append(message)
    fun appendStderrLn(message: String): StringBuilder = stderr.append(message).append("\n")

    @Synchronized
    fun setStateFailed(@NonNull error: Error): Boolean = setStateFailed(error.getType(), error.getCode(), error.getMessage(), null)

    @Synchronized
    fun setStateFailed(@NonNull error: Error, throwable: Throwable): Boolean = setStateFailed(error.getType(), error.getCode(), error.getMessage(), Collections.singletonList(throwable))

    @Synchronized
    fun setStateFailed(@NonNull error: Error, throwablesList: List<Throwable>): Boolean = setStateFailed(error.getType(), error.getCode(), error.getMessage(), throwablesList)

    @Synchronized
    fun setStateFailed(code: Int, message: String): Boolean = setStateFailed(null, code, message, null)

    @Synchronized
    fun setStateFailed(code: Int, message: String, throwable: Throwable): Boolean = setStateFailed(null, code, message, Collections.singletonList(throwable))

    @Synchronized
    fun setStateFailed(code: Int, message: String, throwablesList: List<Throwable>): Boolean = setStateFailed(null, code, message, throwablesList)

    @Synchronized
    fun setStateFailed(type: String?, code: Int, message: String?, throwablesList: List<Throwable>?): Boolean {
        val error = Error()
        errorsList.add(error)
        return error.setStateFailed(type, code, message, throwablesList)
    }

    fun isStateFailed(): Boolean = errorsList.any { it.isStateFailed() }

    fun getErrCode(): Int = errorsList.lastOrNull()?.getCode() ?: Errno.ERRNO_SUCCESS.getCode()

    @NonNull
    override fun toString(): String = getResultDataLogString(this, true)

    companion object {
        fun getResultDataLogString(resultData: ResultData?, logStdoutAndStderr: Boolean): String {
            if (resultData == null) return "null"
            val logString = StringBuilder()
            if (logStdoutAndStderr) {
                logString.append("\n").append(resultData.getStdoutLogString())
                logString.append("\n").append(resultData.getStderrLogString())
            }
            logString.append("\n").append(resultData.getExitCodeLogString())
            logString.append("\n\n").append(getErrorsListLogString(resultData))
            return logString.toString()
        }

        fun getErrorsListLogString(resultData: ResultData?): String {
            if (resultData == null) return "null"
            val logString = StringBuilder()
            resultData.errorsList.forEach { error ->
                if (error.isStateFailed()) {
                    if (logString.isNotEmpty()) logString.append("\n")
                    logString.append(error.getErrorLogString())
                }
            }
            return logString.toString()
        }

        fun getResultDataMarkdownString(resultData: ResultData?): String {
            if (resultData == null) return "null"
            val markdownString = StringBuilder()
            markdownString.append(if (resultData.stdout.isEmpty()) MarkdownUtils.getSingleLineMarkdownStringEntry("Stdout", null, "-") else MarkdownUtils.getMultiLineMarkdownStringEntry("Stdout", resultData.stdout.toString(), "-"))
            markdownString.append("\n").append(if (resultData.stderr.isEmpty()) MarkdownUtils.getSingleLineMarkdownStringEntry("Stderr", null, "-") else MarkdownUtils.getMultiLineMarkdownStringEntry("Stderr", resultData.stderr.toString(), "-"))
            markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Exit Code", resultData.exitCode, "-"))
            markdownString.append("\n\n").append(getErrorsListMarkdownString(resultData))
            return markdownString.toString()
        }

        fun getErrorsListMarkdownString(resultData: ResultData?): String {
            if (resultData == null) return "null"
            val markdownString = StringBuilder()
            resultData.errorsList.forEach { error ->
                if (error.isStateFailed()) {
                    if (markdownString.isNotEmpty()) markdownString.append("\n")
                    markdownString.append(error.getErrorMarkdownString())
                }
            }
            return markdownString.toString()
        }

        fun getErrorsListMinimalString(resultData: ResultData?): String {
            if (resultData == null) return "null"
            val minimalString = StringBuilder()
            resultData.errorsList.forEach { error ->
                if (error.isStateFailed()) {
                    if (minimalString.isNotEmpty()) minimalString.append("\n")
                    minimalString.append(error.getMinimalErrorString())
                }
            }
            return minimalString.toString()
        }
    }

    fun getStdoutLogString(): String = if (stdout.isEmpty()) Logger.getSingleLineLogStringEntry("Stdout", null, "-") else Logger.getMultiLineLogStringEntry("Stdout", DataUtils.getTruncatedCommandOutput(stdout.toString(), Logger.LOGGER_ENTRY_MAX_SAFE_PAYLOAD / 5, false, false, true), "-")
    fun getStderrLogString(): String = if (stderr.isEmpty()) Logger.getSingleLineLogStringEntry("Stderr", null, "-") else Logger.getMultiLineLogStringEntry("Stderr", DataUtils.getTruncatedCommandOutput(stderr.toString(), Logger.LOGGER_ENTRY_MAX_SAFE_PAYLOAD / 5, false, false, true), "-")
    fun getExitCodeLogString(): String = Logger.getSingleLineLogStringEntry("Exit Code", exitCode, "-")
}
