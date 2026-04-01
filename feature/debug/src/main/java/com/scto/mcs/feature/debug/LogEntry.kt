package com.scto.mcs.feature.debug

data class LogEntry(
    val timestamp: String,
    val level: String,
    val tag: String,
    val pid: String,
    val message: String
)
