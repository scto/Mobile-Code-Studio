package com.scto.mcs.termux.application.models

enum class UserAction(val nameString: String) {
    REPORT_ISSUE_FROM_TRANSCRIPT("report issue from transcript");

    fun getName(): String = nameString
}
