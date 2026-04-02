package com.scto.mcs.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.domain.repository.ProjectRepository
import com.scto.mcs.domain.usecase.CloneRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val cloneRepositoryUseCase: CloneRepositoryUseCase
) : ViewModel() {

    private val _projects = MutableStateFlow<List<File>>(emptyList())
    val projects: StateFlow<List<File>> = _projects

    init {
        loadProjects()
    }

    fun loadProjects() {
        _projects.value = projectRepository.getProjects()
    }

    fun createProject(name: String) {
        projectRepository.createProject(name)
        loadProjects()
    }

    fun cloneProject(url: String, destination: File) {
        viewModelScope.launch {
            cloneRepositoryUseCase(url, destination)
            loadProjects()
        }
    }
}
