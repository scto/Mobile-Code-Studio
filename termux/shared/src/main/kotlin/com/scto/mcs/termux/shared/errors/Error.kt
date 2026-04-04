package com.scto.mcs.termux.shared.errors

import android.content.Context
package com.scto.mcs.termux.shared.errors

import android.content.Context
import com.scto.mcs.termux.shared.logger.Logger
import com.scto.mcs.termux.shared.markdown.MarkdownUtils
import java.io.Serializable
import java.util.Collections

class Error(
    var label: String? = null,
    var type: String = Errno.TYPE,
    var code: Int = Errno.ERRNO_SUCCESS.code,
    var message: String? = null,
    initialThrowablesList: List<Throwable>? = null
) : Serializable {

    val throwablesList: MutableList<Throwable> = mutableListOf()

    init {
        initialThrowablesList?.let { this.throwablesList.addAll(it) }
        // Ensure code is at least ERRNO_SUCCESS.code if not explicitly set higher
        if (code < Errno.ERRNO_SUCCESS.code) code = Errno.ERRNO_SUCCESS.code
    }

    companion object {
        private const val LOG_TAG = "Error"

        /**
         * Log the [Error] and show a toast for the minimal [String] for the [Error].
         *
         * @param context The [Context] for operations.
         * @param logTag The log tag to use for logging.
         * @param error The [Error] to convert.
         */
        fun logErrorAndShowToast(context: Context?, logTag: String?, error: Error?) {
            error?.logErrorAndShowToast(context, logTag)
        }

        /**
         * Get a log friendly [String] for [Error] error parameters.
         *
         * @param error The [Error] to convert.
         * @return Returns the log friendly [String].
         */
        fun getErrorLogString(error: Error?): String {
            return error?.getErrorLogString() ?: "null"
        }

        /**
         * Get a minimal log friendly [String] for [Error] error parameters.
         *
         * @param error The [Error] to convert.
         * @return Returns the log friendly [String].
         */
        fun getMinimalErrorLogString(error: Error?): String {
            return error?.getMinimalErrorLogString() ?: "null"
        }

        /**
         * Get a minimal [String] for [Error] error parameters.
         *
         * @param error The [Error] to convert.
         * @return Returns the [String].
         */
        fun getMinimalErrorString(error: Error?): String {
            return error?.getMinimalErrorString() ?: "null"
        }

        /**
         * Get a markdown [String] for [Error].
         *
         * @param error The [Error] to convert.
         * @return Returns the markdown [String].
         */
        fun getErrorMarkdownString(error: Error?): String {
            return error?.getErrorMarkdownString() ?: "null"
        }
    }


    constructor(type: String?, code: Int?, message: String?, throwablesList: List<Throwable>?) :
        this(null, type ?: Errno.TYPE, code ?: Errno.ERRNO_SUCCESS.code, message, throwablesList)

    constructor(type: String?, code: Int?, message: String?, throwable: Throwable?) :
        this(null, type ?: Errno.TYPE, code ?: Errno.ERRNO_SUCCESS.code, message, if (throwable != null) Collections.singletonList(throwable) else null)

    constructor(type: String?, code: Int?, message: String?) :
        this(null, type ?: Errno.TYPE, code ?: Errno.ERRNO_SUCCESS.code, message, null)

    constructor(code: Int?, message: String?, throwablesList: List<Throwable>?) :
        this(null, Errno.TYPE, code ?: Errno.ERRNO_SUCCESS.code, message, throwablesList)

    constructor(code: Int?, message: String?, throwable: Throwable?) :
        this(null, Errno.TYPE, code ?: Errno.ERRNO_SUCCESS.code, message, if (throwable != null) Collections.singletonList(throwable) else null)

    constructor(code: Int?, message: String?) :
        this(null, Errno.TYPE, code ?: Errno.ERRNO_SUCCESS.code, message, null)

    constructor(message: String?, throwable: Throwable?) :
        this(null, Errno.TYPE, Errno.ERRNO_SUCCESS.code, message, if (throwable != null) Collections.singletonList(throwable) else null)

    constructor(message: String?, throwablesList: List<Throwable>?) :
        this(null, Errno.TYPE, Errno.ERRNO_SUCCESS.code, message, throwablesList)

    constructor(message: String?) :
        this(null, Errno.TYPE, Errno.ERRNO_SUCCESS.code, message, null)


    fun setLabel(label: String?): Error {
        this.label = label
        return this
    }

    fun prependMessage(message: String?) {
        if (message != null && isStateFailed())
            this.message = message + (this.message ?: "")
    }

    fun appendMessage(message: String?) {
        if (message != null && isStateFailed())
            this.message = (this.message ?: "") + message
    }


    @Synchronized
    fun setStateFailed(error: Error): Boolean {
        return setStateFailed(error.type, error.code, error.message, null)
    }

    @Synchronized
    fun setStateFailed(error: Error, throwable: Throwable?): Boolean {
        return setStateFailed(error.type, error.code, error.message, if (throwable != null) Collections.singletonList(throwable) else null)
    }

    @Synchronized
    fun setStateFailed(error: Error, throwablesList: List<Throwable>?): Boolean {
        return setStateFailed(error.type, error.code, error.message, throwablesList)
    }

    @Synchronized
    fun setStateFailed(code: Int, message: String?): Boolean {
        return setStateFailed(this.type, code, message, null)
    }

    @Synchronized
    fun setStateFailed(code: Int, message: String?, throwable: Throwable?): Boolean {
        return setStateFailed(this.type, code, message, if (throwable != null) Collections.singletonList(throwable) else null)
    }

    @Synchronized
    fun setStateFailed(code: Int, message: String?, throwablesList: List<Throwable>?): Boolean {
        return setStateFailed(this.type, code, message, throwablesList)
    }

    @Synchronized
    fun setStateFailed(type: String?, code: Int, message: String?, throwablesList: List<Throwable>?): Boolean {
        this.message = message
        this.throwablesList.clear()
        throwablesList?.let { this.throwablesList.addAll(it) }

        if (!type.isNullOrEmpty())
            this.type = type

        return if (code > Errno.ERRNO_SUCCESS.code) {
            this.code = code
            true
        } else {
            Logger.logWarn(LOG_TAG, "Ignoring invalid error code value \"$code\". Force setting it to RESULT_CODE_FAILED \"${Errno.ERRNO_FAILED.code}\"")
            this.code = Errno.ERRNO_FAILED.code
            false
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

        logString.append("(").append(code).append(") ")
        logString.append(type).append(": ").append(message ?: "")

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


    fun getCodeString(): String {
        return Logger.getSingleLineLogStringEntry("Error Code", code, "-")
    }

    fun getTypeAndMessageLogString(): String {
        return Logger.getMultiLineLogStringEntry(if (Errno.TYPE == type) "Error Message" else "Error Message ($type)", message, "-")
    }

    fun geStackTracesLogString(): String {
        return Logger.getStackTracesString("StackTraces:", Logger.getStackTracesStringArray(throwablesList))
    }

    fun geStackTracesMarkdownString(): String {
        return Logger.getStackTracesMarkdownString("StackTraces", Logger.getStackTracesStringArray(throwablesList))
    }

}
