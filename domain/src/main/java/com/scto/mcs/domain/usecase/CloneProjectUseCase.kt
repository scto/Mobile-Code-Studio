package com.scto.mcs.domain.usecase

import com.scto.mcs.domain.repository.GitRepository
import javax.inject.Inject

class CloneProjectUseCase @Inject constructor(private val repository: GitRepository) {
    suspend operator fun invoke(url: String, targetPath: String) = repository.clone(url, targetPath)
}
