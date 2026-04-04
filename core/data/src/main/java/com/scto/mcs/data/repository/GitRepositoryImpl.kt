package com.scto.mcs.data.repository

import com.scto.mcs.domain.repository.GitRepository
import org.eclipse.jgit.api.Git
import java.io.File
import javax.inject.Inject

class GitRepositoryImpl @Inject constructor() : GitRepository {
    
    override fun clone(url: String, destination: File) {
        Git.cloneRepository()
            .setURI(url)
            .setDirectory(destination)
            .call()
    }
}
