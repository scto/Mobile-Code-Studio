package com.scto.mcs.domain.repository

import com.scto.mcs.domain.model.Project
import com.scto.mcs.domain.model.FileNode

interface ProjectRepository {
    fun listProjects(): List<Project>
    fun createProject(name: String, type: String): Project
    fun deleteProject(project: Project)
    fun getProjectTree(project: Project): FileNode
}
