package com.scto.mcs.domain.repository

import java.io.File

interface GitRepository {
    suspend fun clone(url: String, targetPath: String): Result<Unit>
    suspend fun getStatus(projectDir: File): String
    suspend fun commit(projectDir: File, message: String): Boolean
}
