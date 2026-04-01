package com.scto.mcs.domain.usecase

import com.scto.mcs.domain.model.FileNode
import com.scto.mcs.domain.model.Project
import com.scto.mcs.domain.repository.ProjectRepository
import javax.inject.Inject

class GetProjectTreeUseCase @Inject constructor(private val repository: ProjectRepository) {
    operator fun invoke(project: Project): FileNode = repository.getProjectTree(project)
}
