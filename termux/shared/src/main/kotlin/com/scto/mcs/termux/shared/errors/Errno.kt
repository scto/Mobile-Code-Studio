package com.scto.mcs.termux.shared.errors

import android.app.Activity
import com.termux.shared.logger.Logger
import java.util.Arrays
import java.util.Collections

/** The class that defines error messages and codes. */
open class Errno(
    val type: String,
    val code: Int,
    val message: String
) {

    companion object {
        private val map = HashMap<String, Errno>()
        const val TYPE = "Error"

        val ERRNO_SUCCESS = Errno(TYPE, Activity.RESULT_OK, "Success")
        val ERRNO_CANCELLED = Errno(TYPE, Activity.RESULT_CANCELED, "Cancelled")
        val ERRNO_MINOR_FAILURES = Errno(TYPE, Activity.RESULT_FIRST_USER, "Minor failure")
        val ERRNO_FAILED = Errno(TYPE, Activity.RESULT_FIRST_USER + 1, "Failed")

        private const val LOG_TAG = "Errno"

        /**
         * Get the [Errno] of a specific type and code.
         *
         * @param type The unique type of the [Errno].
         * @param code The unique code of the [Errno].
         */
        fun valueOf(type: String?, code: Int?): Errno? {
            if (type.isNullOrEmpty() || code == null) return null
            return map["$type:$code"]
        }
    }

    init {
        map["$type:$code"] = this
    }

    override fun toString(): String {
        return "type=$type, code=$code, message=\"$message\""
    }

    fun getError(): Error {
        return Error(type, code, message)
    }

    fun getError(vararg args: Any?): Error {
        return try {
            Error(type, code, String.format(message, *args))
        } catch (e: Exception) {
            Logger.logWarn(LOG_TAG, "Exception raised while calling String.format() for error message of errno $this with args ${Arrays.toString(args)}\n${e.message}")
            // Return unformatted message as a backup
            Error(type, code, "$message: ${Arrays.toString(args)}")
        }
    }

    fun getError(throwable: Throwable?, vararg args: Any?): Error {
        return if (throwable == null) getError(*args)
        else getError(Collections.singletonList(throwable), *args)
    }

    fun getError(throwablesList: List<Throwable>?, vararg args: Any?): Error {
        return try {
            if (throwablesList == null) Error(type, code, String.format(message, *args))
            else Error(type, code, String.format(message, *args), throwablesList)
        } catch (e: Exception) {
            Logger.logWarn(LOG_TAG, "Exception raised while calling String.format() for error message of errno $this with args ${Arrays.toString(args)}\n${e.message}")
            // Return unformatted message as a backup
            Error(type, code, "$message: ${Arrays.toString(args)}", throwablesList)
        }
    }

    fun equalsErrorTypeAndCode(error: Error?): Boolean {
        if (error == null) return false
        return type == error.type && code == error.code
    }
}
