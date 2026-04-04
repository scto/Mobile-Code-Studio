package com.scto.mcs.termux.shared.errors

import android.content.Context
import com.termux.shared.logger.Logger
import com.termux.shared.markdown.MarkdownUtils
import java.io.Serializable
import java.util.ArrayList
import java.util.Collections

class Error : Serializable {

    var label: String? = null
        private set
    var type: String = Errno.TYPE
        private set
    var code: Int = Errno.ERRNO_SUCCESS.code
        private set
    var message: String? = null
        private set
    var throwablesList: List<Throwable> = ArrayList()
        private set

    companion object {
        private const val LOG_TAG = "Error"
    }

    constructor() {
        initError(null, null, null, null)
    }

    constructor(type: String?, code: Int?, message: String?, throwablesList: List<Throwable>?) {
        initError(type, code, message, throwablesList)
    }

    constructor(type: String?, code: Int?, message: String?, throwable: Throwable?) {
        initError(type, code, message, throwable?.let { Collections.singletonList(it) })
    }

    constructor(type: String?, code: Int?, message: String?) {
        initError(type, code, message, null)
    }

    constructor(code: Int?, message: String?, throwablesList: List<Throwable>?) {
        initError(null, code, message, throwablesList)
    }

    constructor(code: Int?, message: String?, throwable: Throwable?) {
        initError(null, code, message, throwable?.let { Collections.singletonList(it) })
    }

    constructor(code: Int?, message: String?) {
        initError(null, code, message, null)
    }

    constructor(message: String?, throwable: Throwable?) {
        initError(null, null, message, throwable?.let { Collections.singletonList(it) })
    }

    constructor(message: String?, throwablesList: List<Throwable>?) {
        initError(null, null, message, throwablesList)
    }

    constructor(message: String?) {
        initError(null, null, message, null)
    }

    private fun initError(type: String?, code: Int?, message: String?, throwablesList: List<Throwable>?) {
        this.type = if (!type.isNullOrEmpty()) type else Errno.TYPE
        this.code = if (code != null && code > Errno.ERRNO_SUCCESS.code) code else Errno.ERRNO_SUCCESS.code
        this.message = message
        if (throwablesList != null) this.throwablesList = throwablesList
    }

    fun setLabel(label: String?): Error {
        this.label = label
        return this
    }

    fun prependMessage(message: String?) {
        if (message != null && isStateFailed())
            this.message = message + this.message
    }

    fun appendMessage(message: String?) {
        if (message != null && isStateFailed())
            this.message = this.message + message
    }

    @Synchronized
    fun setStateFailed(error: Error): Boolean {
        return setStateFailed(error.type, error.code, error.message, null)
    }

    @Synchronized
    fun setStateFailed(error: Error, throwable: Throwable): Boolean {
        return setStateFailed(error.type, error.code, error.message, Collections.singletonList(throwable))
    }

    @Synchronized
    fun setStateFailed(error: Error, throwablesList: List<Throwable>): Boolean {
        return setStateFailed(error.type, error.code, error.message, throwablesList)
    }

    @Synchronized
    fun setStateFailed(code: Int, message: String?): Boolean {
        return setStateFailed(this.type, code, message, null)
    }

    @Synchronized
    fun setStateFailed(code: Int, message: String?, throwable: Throwable): Boolean {
        return setStateFailed(this.type, code, message, Collections.singletonList(throwable))
    }

    @Synchronized
    fun setStateFailed(code: Int, message: String?, throwablesList: List<Throwable>): Boolean {
        return setStateFailed(this.type, code, message, throwablesList)
    }

    @Synchronized
    fun setStateFailed(type: String?, code: Int, message: String?, throwablesList: List<Throwable>?): Boolean {
        this.message = message
        this.throwablesList = throwablesList ?: emptyList()

        if (!type.isNullOrEmpty())
            this.type = type

        if (code > Errno.ERRNO_SUCCESS.code) {
            this.code = code
            return true
        } else {
            Logger.logWarn(LOG_TAG, "Ignoring invalid error code value \"$code\". Force setting it to RESULT_CODE_FAILED \"${Errno.ERRNO_FAILED.code}\"")
            this.code = Errno.ERRNO_FAILED.code
            return false
        }
    }

    fun isStateFailed(): Boolean {
        return code > Errno.ERRNO_SUCCESS.code
    }

    override fun toString(): String {
        return getErrorLogString()
    }

    fun logErrorAndShowToast(context: Context?, logTag: String?) {
        Logger.logErrorExtended(logTag, getErrorLogString())
        Logger.showToast(context, getMinimalErrorLogString(), true)
    }

    fun getErrorLogString(): String {
        val logString = StringBuilder()
        logString.append(getCodeString())
        logString.append("\n").append(getTypeAndMessageLogString())
        if (throwablesList.isNotEmpty())
            logString.append("\n").append(getStackTracesLogString())
        return logString.toString()
    }

    fun getMinimalErrorLogString(): String {
        val logString = StringBuilder()
        logString.append(getCodeString())
        logString.append(getTypeAndMessageLogString())
        return logString.toString()
    }

    fun getMinimalErrorString(): String {
        val logString = StringBuilder()
        logString.append("($code) ")
        logString.append("$type: $message")
        return logString.toString()
    }

    fun getErrorMarkdownString(): String {
        val markdownString = StringBuilder()
        markdownString.append(MarkdownUtils.getSingleLineMarkdownStringEntry("Error Code", code, "-"))
        markdownString.append("\n").append(MarkdownUtils.getMultiLineMarkdownStringEntry(
            (if (Errno.TYPE == type) "Error Message" else "Error Message ($type)"), message, "-"))
        if (throwablesList.isNotEmpty())
            markdownString.append("\n\n").append(getStackTracesMarkdownString())
        return markdownString.toString()
    }

    fun getCodeString(): String {
        return Logger.getSingleLineLogStringEntry("Error Code", code, "-")
    }

    fun getTypeAndMessageLogString(): String {
        return Logger.getMultiLineLogStringEntry(if (Errno.TYPE == type) "Error Message" else "Error Message ($type)", message, "-")
    }

    fun getStackTracesLogString(): String {
        return Logger.getStackTracesString("StackTraces:", Logger.getStackTracesStringArray(throwablesList))
    }

    fun getStackTracesMarkdownString(): String {
        return Logger.getStackTracesMarkdownString("StackTraces", Logger.getStackTracesStringArray(throwablesList))
    }
}
