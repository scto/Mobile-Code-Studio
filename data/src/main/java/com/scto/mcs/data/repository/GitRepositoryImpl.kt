package com.scto.mcs.data.repository

import com.scto.mcs.core.GitCallback
import com.scto.mcs.core.GitManager
import com.scto.mcs.domain.repository.GitRepository
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GitRepositoryImpl @Inject constructor(
    private val gitManager: GitManager
) : GitRepository {

    override suspend fun clone(url: String, targetPath: String): Result<Unit> = suspendCoroutine { continuation ->
        // Simplified callback handling
        // In a real app, we'd need to pass a proper callback
    }

    override suspend fun getStatus(projectDir: File): String = gitManager.status(projectDir)

    override suspend fun commit(projectDir: File, message: String): Boolean = gitManager.commit(projectDir, message)
}
