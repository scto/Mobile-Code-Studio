package com.scto.mcs.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.transport.CredentialsProvider
import java.io.File

interface GitCallback {
    fun onProgress(progress: Float, message: String)
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
            Git.cloneRepository()
                .setURI(url)
                .setDirectory(targetDir)
                .setCredentialsProvider(credentials)
                .setProgressMonitor(object : ProgressMonitor {
                    private var totalWork = 0
                    private var currentWork = 0

                    override fun start(totalTasks: Int) {}
                    override fun beginTask(title: String, totalWork: Int) {
                        this.totalWork = totalWork
                        this.currentWork = 0
                        callback.onProgress(0f, title)
                    }
                    override fun update(completed: Int) {
                        currentWork += completed
                        if (totalWork > 0) {
                            callback.onProgress(currentWork.toFloat() / totalWork, "Cloning...")
                        }
                    }
                    override fun endTask() {}
                    override fun isCancelled(): Boolean = false
                })
                .call()
                .use {
                    callback.onSuccess()
                }
        } catch (e: GitAPIException) {
            callback.onError(e.message ?: "Git error occurred")
        } catch (e: Exception) {
            callback.onError("Unexpected error: ${e.message}")
        }
    }
}
