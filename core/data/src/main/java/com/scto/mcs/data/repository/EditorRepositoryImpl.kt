package com.scto.mcs.data.repository

import com.scto.mcs.domain.repository.EditorRepository
import java.io.File
import javax.inject.Inject

class EditorRepositoryImpl @Inject constructor() : EditorRepository {
    
    override fun readFile(file: File): String {
        return file.readText()
    }

    override fun saveFile(file: File, content: String) {
        file.writeText(content)
    }
}
