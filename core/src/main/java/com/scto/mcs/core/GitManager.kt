package com.scto.mcs.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.CredentialsProvider
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface GitCallback {
    fun onProgress(progress: Float, message: String)
    fun onError(error: String)
    fun onSuccess()
}

@Singleton
class GitManager @Inject constructor() {

    suspend fun clone(
        url: String,
        targetDir: File,
        credentials: CredentialsProvider?,
        callback: GitCallback
    ) = withContext(Dispatchers.IO) {
        try {
            Git.cloneRepository()
                .setURI(url)
                .setDirectory(targetDir)
                .setCredentialsProvider(credentials)
                .call()
                .use {
                    callback.onSuccess()
                }
        } catch (e: GitAPIException) {
            callback.onError(e.message ?: "Git error occurred")
        }
    }

    suspend fun status(projectDir: File): String = withContext(Dispatchers.IO) {
        try {
            Git.open(projectDir).use { git ->
                val status = git.status().call()
                "Added: ${status.added}, Changed: ${status.changed}, Missing: ${status.missing}"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    suspend fun commit(projectDir: File, message: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Git.open(projectDir).use { git ->
                git.add().addFilepattern(".").call()
                git.commit().setMessage(message).call()
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}
