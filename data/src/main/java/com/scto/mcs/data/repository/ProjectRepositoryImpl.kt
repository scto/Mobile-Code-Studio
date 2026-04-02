package com.scto.mcs.data.repository

import com.scto.mcs.core.FileSystemUtils
import com.scto.mcs.domain.repository.ProjectRepository
import java.io.File
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val fileSystemUtils: FileSystemUtils
) : ProjectRepository {
    
    override fun getProjects(): List<File> {
        return fileSystemUtils.rootDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
    }

    override fun createProject(name: String): File {
        val projectDir = fileSystemUtils.getFile(name)
        projectDir.mkdirs()
        return projectDir
    }
}
