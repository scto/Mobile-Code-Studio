package com.scto.mcs.termux.shared.logger

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.scto.mcs.core.resources.R
import com.scto.mcs.termux.shared.data.DataUtils
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.ArrayList
import java.util.Collections

object Logger {

    private var DEFAULT_LOG_TAG = "Logger"

    const val LOG_LEVEL_OFF = 0
    const val LOG_LEVEL_NORMAL = 1
    const val LOG_LEVEL_DEBUG = 2
    const val LOG_LEVEL_VERBOSE = 3

    const val DEFAULT_LOG_LEVEL = LOG_LEVEL_NORMAL
    const val MAX_LOG_LEVEL = LOG_LEVEL_VERBOSE
    private var CURRENT_LOG_LEVEL = DEFAULT_LOG_LEVEL

    const val LOGGER_ENTRY_MAX_PAYLOAD = 4068
    const val LOGGER_ENTRY_MAX_SAFE_PAYLOAD = 4000

    fun logMessage(logPriority: Int, tag: String?, message: String?) {
        if (message == null) return
        val fullTag = getFullTag(tag)
        when (logPriority) {
            Log.ERROR -> if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) Log.e(fullTag, message)
            Log.WARN -> if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) Log.w(fullTag, message)
            Log.INFO -> if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) Log.i(fullTag, message)
            Log.DEBUG -> if (CURRENT_LOG_LEVEL >= LOG_LEVEL_DEBUG) Log.d(fullTag, message)
            Log.VERBOSE -> if (CURRENT_LOG_LEVEL >= LOG_LEVEL_VERBOSE) Log.v(fullTag, message)
        }
    }

    fun logExtendedMessage(logLevel: Int, tag: String?, message: String?) {
        if (message == null) return

        val fullTag = getFullTag(tag)
        val maxEntrySize = LOGGER_ENTRY_MAX_PAYLOAD - 8 - fullTag.length - 4
        val messagesList = ArrayList<String>()
        var currentMessage = message

        while (currentMessage!!.isNotEmpty()) {
            if (currentMessage.length > maxEntrySize) {
                var cutOffIndex = maxEntrySize
                val nextNewlineIndex = currentMessage.lastIndexOf('\n', cutOffIndex)
                if (nextNewlineIndex != -1) {
                    cutOffIndex = nextNewlineIndex + 1
                }
                messagesList.add(currentMessage.substring(0, cutOffIndex))
                currentMessage = currentMessage.substring(cutOffIndex)
            } else {
                messagesList.add(currentMessage)
                break
            }
        }

        for (i in messagesList.indices) {
            val prefix = if (messagesList.size > 1) "(${i + 1}/${messagesList.size})\n" else ""
            logMessage(logLevel, tag, prefix + messagesList[i])
        }
    }

    fun logError(tag: String?, message: String?) = logMessage(Log.ERROR, tag, message)
    fun logError(message: String?) = logMessage(Log.ERROR, DEFAULT_LOG_TAG, message)
    fun logErrorExtended(tag: String?, message: String?) = logExtendedMessage(Log.ERROR, tag, message)
    fun logErrorExtended(message: String?) = logExtendedMessage(Log.ERROR, DEFAULT_LOG_TAG, message)

    fun logErrorPrivate(tag: String?, message: String?) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_DEBUG) logMessage(Log.ERROR, tag, message)
    }
    fun logErrorPrivate(message: String?) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_DEBUG) logMessage(Log.ERROR, DEFAULT_LOG_TAG, message)
    }
    fun logErrorPrivateExtended(tag: String?, message: String?) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_DEBUG) logExtendedMessage(Log.ERROR, tag, message)
    }
    fun logErrorPrivateExtended(message: String?) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_DEBUG) logExtendedMessage(Log.ERROR, DEFAULT_LOG_TAG, message)
    }

    fun logWarn(tag: String?, message: String?) = logMessage(Log.WARN, tag, message)
    fun logWarn(message: String?) = logMessage(Log.WARN, DEFAULT_LOG_TAG, message)
    fun logWarnExtended(tag: String?, message: String?) = logExtendedMessage(Log.WARN, tag, message)
    fun logWarnExtended(message: String?) = logExtendedMessage(Log.WARN, DEFAULT_LOG_TAG, message)

    fun logInfo(tag: String?, message: String?) = logMessage(Log.INFO, tag, message)
    fun logInfo(message: String?) = logMessage(Log.INFO, DEFAULT_LOG_TAG, message)
    fun logInfoExtended(tag: String?, message: String?) = logExtendedMessage(Log.INFO, tag, message)
    fun logInfoExtended(message: String?) = logExtendedMessage(Log.INFO, DEFAULT_LOG_TAG, message)

    fun logDebug(tag: String?, message: String?) = logMessage(Log.DEBUG, tag, message)
    fun logDebug(message: String?) = logMessage(Log.DEBUG, DEFAULT_LOG_TAG, message)
    fun logDebugExtended(tag: String?, message: String?) = logExtendedMessage(Log.DEBUG, tag, message)
    fun logDebugExtended(message: String?) = logExtendedMessage(Log.DEBUG, DEFAULT_LOG_TAG, message)

    fun logVerbose(tag: String?, message: String?) = logMessage(Log.VERBOSE, tag, message)
    fun logVerbose(message: String?) = logMessage(Log.VERBOSE, DEFAULT_LOG_TAG, message)
    fun logVerboseExtended(tag: String?, message: String?) = logExtendedMessage(Log.VERBOSE, tag, message)
    fun logVerboseExtended(message: String?) = logExtendedMessage(Log.VERBOSE, DEFAULT_LOG_TAG, message)
    fun logVerboseForce(tag: String?, message: String?) = Log.v(tag, message)

    fun logInfoAndShowToast(context: Context?, tag: String?, message: String?) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) {
            logInfo(tag, message)
            showToast(context, message, true)
        }
    }
    fun logInfoAndShowToast(context: Context?, message: String?) = logInfoAndShowToast(context, DEFAULT_LOG_TAG, message)

    fun logErrorAndShowToast(context: Context?, tag: String?, message: String?) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) {
            logError(tag, message)
            showToast(context, message, true)
        }
    }
    fun logErrorAndShowToast(context: Context?, message: String?) = logErrorAndShowToast(context, DEFAULT_LOG_TAG, message)

    fun logDebugAndShowToast(context: Context?, tag: String?, message: String?) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_DEBUG) {
            logDebug(tag, message)
            showToast(context, message, true)
        }
    }
    fun logDebugAndShowToast(context: Context?, message: String?) = logDebugAndShowToast(context, DEFAULT_LOG_TAG, message)

    fun logStackTraceWithMessage(tag: String?, message: String?, throwable: Throwable?) {
        logErrorExtended(tag, getMessageAndStackTraceString(message, throwable))
    }
    fun logStackTraceWithMessage(message: String?, throwable: Throwable?) = logStackTraceWithMessage(DEFAULT_LOG_TAG, message, throwable)
    fun logStackTrace(tag: String?, throwable: Throwable?) = logStackTraceWithMessage(tag, null, throwable)
    fun logStackTrace(throwable: Throwable?) = logStackTraceWithMessage(DEFAULT_LOG_TAG, null, throwable)

    fun logStackTracesWithMessage(tag: String?, message: String?, throwablesList: List<Throwable>?) {
        logErrorExtended(tag, getMessageAndStackTracesString(message, throwablesList))
    }

    fun getMessageAndStackTraceString(message: String?, throwable: Throwable?): String? {
        if (message == null && throwable == null) return null
        if (message != null && throwable != null) return "$message:\n${getStackTraceString(throwable)}"
        return throwable?.let { getStackTraceString(it) } ?: message
    }

    fun getMessageAndStackTracesString(message: String?, throwablesList: List<Throwable>?): String? {
        if (message == null && throwablesList.isNullOrEmpty()) return null
        if (message != null && !throwablesList.isNullOrEmpty()) return "$message:\n${getStackTracesString(null, getStackTracesStringArray(throwablesList))}"
        return if (throwablesList.isNullOrEmpty()) message else getStackTracesString(null, getStackTracesStringArray(throwablesList))
    }

    fun getStackTraceString(throwable: Throwable?): String? {
        if (throwable == null) return null
        return try {
            val errors = StringWriter()
            val pw = PrintWriter(errors)
            throwable.printStackTrace(pw)
            pw.close()
            errors.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getStackTracesStringArray(throwable: Throwable?): Array<String?> = getStackTracesStringArray(Collections.singletonList(throwable))

    fun getStackTracesStringArray(throwablesList: List<Throwable>?): Array<String?> {
        if (throwablesList == null) return emptyArray()
        val stackTraceStringArray = arrayOfNulls<String>(throwablesList.size)
        for (i in throwablesList.indices) {
            stackTraceStringArray[i] = getStackTraceString(throwablesList[i])
        }
        return stackTraceStringArray
    }

    fun getStackTracesString(label: String?, stackTraceStringArray: Array<String?>): String {
        val actualLabel = label ?: "StackTraces:"
        val stackTracesString = StringBuilder(actualLabel)
        if (stackTraceStringArray.isEmpty()) {
            stackTracesString.append(" -")
        } else {
            for (i in stackTraceStringArray.indices) {
                if (stackTraceStringArray.size > 1) stackTracesString.append("\n\nStacktrace ${i + 1}")
                stackTracesString.append("\n```\n").append(stackTraceStringArray[i]).append("\n```\n")
            }
        }
        return stackTracesString.toString()
    }

    fun getStackTracesMarkdownString(label: String?, stackTraceStringArray: Array<String?>): String {
        val actualLabel = label ?: "StackTraces"
        val stackTracesString = StringBuilder("### $actualLabel")
        if (stackTraceStringArray.isEmpty()) {
            stackTracesString.append("\n\n`-`")
        } else {
            for (i in stackTraceStringArray.indices) {
                if (stackTraceStringArray.size > 1) stackTracesString.append("\n\n\n#### Stacktrace ${i + 1}")
                stackTracesString.append("\n\n```\n").append(stackTraceStringArray[i]).append("\n```")
            }
        }
        stackTracesString.append("\n##\n")
        return stackTracesString.toString()
    }

    fun getSingleLineLogStringEntry(label: String, obj: Any?, def: String): String {
        return if (obj != null) "$label: `$obj`" else "$label: $def"
    }

    fun getMultiLineLogStringEntry(label: String, obj: Any?, def: String): String {
        return if (obj != null) "$label:\n```\n$obj\n```\n" else "$label: $def"
    }

    fun showToast(context: Context?, toastText: String?, longDuration: Boolean) {
        if (context == null || DataUtils.isNullOrEmpty(toastText)) return
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, toastText, if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
        }
    }

    fun getLogLevelsArray(): IntArray = intArrayOf(LOG_LEVEL_OFF, LOG_LEVEL_NORMAL, LOG_LEVEL_DEBUG, LOG_LEVEL_VERBOSE)

    fun getLogLevelLabelsArray(context: Context, logLevels: IntArray?, addDefaultTag: Boolean): Array<CharSequence?>? {
        if (logLevels == null) return null
        val logLevelLabels = arrayOfNulls<CharSequence>(logLevels.size)
        for (i in logLevels.indices) {
            logLevelLabels[i] = getLogLevelLabel(context, logLevels[i], addDefaultTag)
        }
        return logLevelLabels
    }

    fun getLogLevelLabel(context: Context, logLevel: Int, addDefaultTag: Boolean): String {
        val logLabel = when (logLevel) {
            LOG_LEVEL_OFF -> context.getString(R.string.log_level_off)
            LOG_LEVEL_NORMAL -> context.getString(R.string.log_level_normal)
            LOG_LEVEL_DEBUG -> context.getString(R.string.log_level_debug)
            LOG_LEVEL_VERBOSE -> context.getString(R.string.log_level_verbose)
            else -> context.getString(R.string.log_level_unknown)
        }
        return if (addDefaultTag && logLevel == DEFAULT_LOG_LEVEL) "$logLabel (default)" else logLabel
    }

    fun getDefaultLogTag(): String = DEFAULT_LOG_TAG

    fun setDefaultLogTag(defaultLogTag: String) {
        DEFAULT_LOG_TAG = if (defaultLogTag.length >= 23) defaultLogTag.substring(0, 22) else defaultLogTag
    }

    fun getLogLevel(): Int = CURRENT_LOG_LEVEL

    fun setLogLevel(context: Context?, logLevel: Int): Int {
        CURRENT_LOG_LEVEL = if (isLogLevelValid(logLevel)) logLevel else DEFAULT_LOG_LEVEL
        if (context != null) {
            showToast(context, context.getString(R.string.log_level_value, getLogLevelLabel(context, CURRENT_LOG_LEVEL, false)), true)
        }
        return CURRENT_LOG_LEVEL
    }

    fun getFullTag(tag: String?): String {
        return if (DEFAULT_LOG_TAG == tag) tag!! else "$DEFAULT_LOG_TAG.$tag"
    }

    fun isLogLevelValid(logLevel: Int?): Boolean {
        return logLevel != null && logLevel >= LOG_LEVEL_OFF && logLevel <= MAX_LOG_LEVEL
    }

    fun shouldEnableLoggingForCustomLogLevel(customLogLevel: Int?): Boolean {
        if (CURRENT_LOG_LEVEL <= LOG_LEVEL_OFF) return false
        if (customLogLevel == null) return CURRENT_LOG_LEVEL >= LOG_LEVEL_VERBOSE
        if (customLogLevel <= LOG_LEVEL_OFF) return false
        val level = if (isLogLevelValid(customLogLevel)) customLogLevel else LOG_LEVEL_VERBOSE
        return level >= CURRENT_LOG_LEVEL
    }
}
