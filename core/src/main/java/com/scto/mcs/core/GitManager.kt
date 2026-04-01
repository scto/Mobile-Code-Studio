package com.scto.mcs.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.CredentialsProvider
import java.io.File

interface GitCallback {
    fun onProgress(progress: Float, message: String)
    fun onError(error: String)
    fun onSuccess()
}

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
}
