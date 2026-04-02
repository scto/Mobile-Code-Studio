package com.scto.mcs.data.repository

import com.scto.mcs.core.FileSystemUtils
import com.scto.mcs.domain.model.FileNode
import com.scto.mcs.domain.model.Project
import com.scto.mcs.domain.repository.ProjectRepository
import java.io.File
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val fileSystemUtils: FileSystemUtils
) : ProjectRepository {

    override fun listProjects(): List<Project> {
        return fileSystemUtils.listFiles(fileSystemUtils.getRootDirectory())
            .filter { it.isDirectory }
            .map { Project(it.name, it.absolutePath, "Unknown") }
    }

    override fun createProject(name: String, type: String): Project {
        val dir = File(fileSystemUtils.getRootDirectory(), name)
        dir.mkdirs()
        return Project(name, dir.absolutePath, type)
    }

    override fun deleteProject(project: Project) {
        val file = File(project.path)
        if (file.exists()) {
            file.deleteRecursively()
        }
    }

    override fun getProjectTree(project: Project): FileNode {
        val root = File(project.path)
        return mapFileToNode(root)
    }

    private fun mapFileToNode(file: File): FileNode {
        return FileNode(
            name = file.name,
            path = file.absolutePath,
            isDirectory = file.isDirectory,
            children = if (file.isDirectory) file.listFiles()?.map { mapFileToNode(it) } ?: emptyList() else emptyList()
        )
    }
}
