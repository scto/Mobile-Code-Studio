package com.scto.mcs.domain.dashboard.model

data class ProjectTemplate(
    val id: String,
    val name: String,
    val description: String,
    val iconResId: Int? = null // Optional icon resource ID
)
