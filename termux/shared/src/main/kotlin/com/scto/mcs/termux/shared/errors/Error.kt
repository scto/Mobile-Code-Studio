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