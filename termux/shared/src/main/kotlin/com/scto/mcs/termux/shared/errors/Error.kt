package com.scto.mcs.termux.shared.errors

import android.content.Context
import androidx.annotation.NonNull
import com.scto.mcs.termux.shared.logger.Logger
import com.scto.mcs.termux.shared.markdown.MarkdownUtils
import java.io.Serializable
import java.util.ArrayList
import java.util.Collections

class Error : Serializable {

    private var label: String? = null
    private var type: String = Errno.TYPE
    private var code: Int = 0
    private var message: String? = null
    private var throwablesList: List<Throwable> = ArrayList()

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
        this.code = if (code != null && code > 0) code else 0 // Assuming 0 is success
        this.message = message
        this.throwablesList = throwablesList ?: ArrayList()
    }

    fun setLabel(label: String?): Error {
        this.label = label
        return this
    }

    fun getLabel(): String? = label
    fun getType(): String = type
    fun getCode(): Int = code
    fun getMessage(): String? = message

    fun prependMessage(message: String?) {
        if (message != null && isStateFailed()) {
            this.message = message + (this.message ?: "")
        }
    }

    fun appendMessage(message: String?) {
        if (message != null && isStateFailed()) {
            this.message = (this.message ?: "") + message
        }
    }

    fun getThrowablesList(): List<Throwable> = Collections.unmodifiableList(throwablesList)

    @Synchronized
    fun setStateFailed(@NonNull error: Error): Boolean {
        return setStateFailed(error.type, error.code, error.message, null)
    }

    @Synchronized
    fun setStateFailed(@NonNull error: Error, throwable: Throwable): Boolean {
        return setStateFailed(error.type, error.code, error.message, Collections.singletonList(throwable))
    }

    @Synchronized
    fun setStateFailed(@NonNull error: Error, throwablesList: List<Throwable>): Boolean {
        return setStateFailed(error.type, error.code, error.message, throwablesList)
    }

    @Synchronized
    fun setStateFailed(code: Int, message: String): Boolean {
        return setStateFailed(this.type, code, message, null)
    }

    @Synchronized
    fun setStateFailed(code: Int, message: String, throwable: Throwable): Boolean {
        return setStateFailed(this.type, code, message, Collections.singletonList(throwable))
    }

    @Synchronized
    fun setStateFailed(code: Int, message: String, throwablesList: List<Throwable>): Boolean {
        return setStateFailed(this.type, code, message, throwablesList)
    }

    @Synchronized
    fun setStateFailed(type: String?, code: Int, message: String?, throwablesList: List<Throwable>?): Boolean {
        this.message = message
        this.throwablesList = throwablesList ?: ArrayList()

        if (!type.isNullOrEmpty()) this.type = type

        if (code > 0) {
            this.code = code
            return true
        } else {
            Logger.logWarn(LOG_TAG, "Ignoring invalid error code value \"$code\". Force setting it to RESULT_CODE_FAILED")
            this.code = 1 // Assuming 1 is failed
            return false
        }
    }

    fun isStateFailed(): Boolean = code > 0

    @NonNull
    override fun toString(): String = getErrorLogString()

    fun logErrorAndShowToast(context: Context, logTag: String) {
        Logger.logErrorExtended(logTag, getErrorLogString())
        Logger.showToast(context, getMinimalErrorLogString(), true)
    }

    fun getErrorLogString(): String {
        val logString = StringBuilder()
        logString.append(getCodeString())
        logString.append("\n").append(getTypeAndMessageLogString())
        if (throwablesList.isNotEmpty())
            logString.append("\n").append(geStackTracesLogString())
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
            if (Errno.TYPE == type) "Error Message" else "Error Message ($type)", message, "-"))
        if (throwablesList.isNotEmpty())
            markdownString.append("\n\n").append(geStackTracesMarkdownString())
        return markdownString.toString()
    }

    fun getCodeString(): String = Logger.getSingleLineLogStringEntry("Error Code", code, "-")

    fun getTypeAndMessageLogString(): String = Logger.getMultiLineLogStringEntry(
        if (Errno.TYPE == type) "Error Message" else "Error Message ($type)", message, "-")

    fun geStackTracesLogString(): String = Logger.getStackTracesString("StackTraces:", Logger.getStackTracesStringArray(throwablesList))

    fun geStackTracesMarkdownString(): String = Logger.getStackTracesMarkdownString("StackTraces", Logger.getStackTracesStringArray(throwablesList))
}
