package com.scto.mcs.data.repository

import com.scto.mcs.core.EditorConfigManager
import com.scto.mcs.core.FileSystemUtils
import com.scto.mcs.domain.repository.EditorRepository
import java.io.File
import javax.inject.Inject

class EditorRepositoryImpl @Inject constructor(
    private val fileSystemUtils: FileSystemUtils,
    private val editorConfigManager: EditorConfigManager
) : EditorRepository {

    override fun readFile(file: File): String = fileSystemUtils.readFile(file)

    override fun writeFile(file: File, content: String) = fileSystemUtils.writeFile(file, content)

    override fun getLanguageConfig(file: File): String = editorConfigManager.getLanguageForFile(file).toString()
}
