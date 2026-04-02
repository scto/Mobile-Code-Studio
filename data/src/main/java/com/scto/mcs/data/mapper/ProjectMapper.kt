package com.scto.mcs.data.mapper

import com.scto.mcs.domain.model.Project
import java.io.File

object ProjectMapper {
    fun toDomain(file: File): Project {
        return Project(
            name = file.name,
            path = file.absolutePath,
            type = "Unknown" // Default type
        )
    }
}
