package com.scto.mcs.domain.usecase

import com.scto.mcs.domain.repository.EditorRepository
import java.io.File
import javax.inject.Inject

class LoadFileContentUseCase @Inject constructor(
    private val repository: EditorRepository
) {
    operator fun invoke(file: File): String {
        return repository.readFile(file)
    }
}
