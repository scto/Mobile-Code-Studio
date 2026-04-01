package com.scto.mcs.feature.debug

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor(
    private val logcatManager: LogcatManager
) : ViewModel() {

    val logs = logcatManager.logs

    fun startLogging() {
        logcatManager.startLogging()
    }

    fun stopLogging() {
        logcatManager.stopLogging()
    }

    fun clearLogs() {
        logcatManager.clearLogs()
    }

    override fun onCleared() {
        super.onCleared()
        logcatManager.stopLogging()
    }
}
