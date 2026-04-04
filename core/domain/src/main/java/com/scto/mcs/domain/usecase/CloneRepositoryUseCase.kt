package com.scto.mcs.domain.usecase

import com.scto.mcs.domain.repository.GitRepository
import java.io.File
import javax.inject.Inject

class CloneRepositoryUseCase @Inject constructor(
    private val repository: GitRepository
) {
    operator fun invoke(url: String, destination: File) {
        repository.clone(url, destination)
    }
}
