package com.scto.mcs.domain.model

sealed class BuildStatus {
    object Idle : BuildStatus()
    data class Building(val progress: Float) : BuildStatus()
    data class Success(val message: String) : BuildStatus()
    data class Error(val message: String) : BuildStatus()
}
