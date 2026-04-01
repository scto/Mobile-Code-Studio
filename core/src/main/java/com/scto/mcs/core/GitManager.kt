package com.scto.mcs.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import java.io.File

interface GitCallback {
    fun onProgress(message: String)
    fun onError(error: String)
    fun onSuccess()
}

class GitManager {

    suspend fun clone(
        url: String,
        targetDir: File,
        credentials: CredentialsProvider?,
        callback: GitCallback
    ) = withContext(Dispatchers.IO) {
        try {
            callback.onProgress("Cloning into ${targetDir.absolutePath}...")
            Git.cloneRepository()
                .setURI(url)
                .setDirectory(targetDir)
                .setCredentialsProvider(credentials)
                .call()
                .use {
                    callback.onSuccess()
                }
        } catch (e: Exception) {
            callback.onError(e.message ?: "Failed to clone repository")
        }
    }

    suspend fun pull(repoDir: File, credentials: CredentialsProvider?, callback: GitCallback) = withContext(Dispatchers.IO) {
        try {
            Git.open(repoDir).use { git ->
                git.pull().setCredentialsProvider(credentials).call()
                callback.onSuccess()
            }
        } catch (e: Exception) {
            callback.onError(e.message ?: "Failed to pull")
        }
    }

    suspend fun commit(repoDir: File, message: String, callback: GitCallback) = withContext(Dispatchers.IO) {
        try {
            Git.open(repoDir).use { git ->
                git.commit().setMessage(message).call()
                callback.onSuccess()
            }
        } catch (e: Exception) {
            callback.onError(e.message ?: "Failed to commit")
        }
    }
}
