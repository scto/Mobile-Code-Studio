package com.scto.mcs.core.model

data class ProjectConfig(
    val projectName: String,
    val packageName: String,
    val targetDir: String,
    val templateType: String
)
