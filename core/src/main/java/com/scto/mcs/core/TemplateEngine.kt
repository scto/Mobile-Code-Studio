package com.scto.mcs.core

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateEngine @Inject constructor() {

    fun generateProject(projectName: String, packageName: String, targetDir: File, templateType: String) {
        if (!targetDir.exists()) targetDir.mkdirs()

        val files = when (templateType) {
            "Empty Compose Activity" -> mapOf(
                "app/build.gradle.kts" to "plugins { id('com.android.application') }\nandroid { namespace = '$packageName' }",
                "app/src/main/kotlin/MainActivity.kt" to "package $packageName\nclass MainActivity : ComponentActivity() {}"
            )
            else -> emptyMap()
        }

        files.forEach { (path, content) ->
            val file = File(targetDir, path)
            file.parentFile?.mkdirs()
            file.writeText(content)
        }
    }
}
