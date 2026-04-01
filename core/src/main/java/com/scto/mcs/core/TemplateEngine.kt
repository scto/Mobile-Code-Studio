package com.scto.mcs.core

import com.scto.mcs.core.model.ProjectConfig
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateEngine @Inject constructor() {

    fun generateProject(config: ProjectConfig) {
        val rootDir = File(config.targetDir)
        if (!rootDir.exists()) rootDir.mkdirs()

        // Define template structure
        val files = when (config.templateType) {
            "Empty Compose Activity" -> mapOf(
                "app/build.gradle.kts" to "plugins { id('com.android.application') }\nandroid { namespace = '${config.packageName}' }",
                "app/src/main/AndroidManifest.xml" to "<manifest package='${config.packageName}'></manifest>",
                "app/src/main/kotlin/MainActivity.kt" to "package ${config.packageName}\nclass MainActivity : ComponentActivity() {}"
            )
            else -> emptyMap()
        }

        files.forEach { (path, content) ->
            val file = File(rootDir, path)
            file.parentFile?.mkdirs()
            val processedContent = content
                .replace("\${PROJECT_NAME}", config.projectName)
                .replace("\${PACKAGE_NAME}", config.packageName)
            file.writeText(processedContent)
        }

        // Inject dummy gradlew
        val gradlew = File(rootDir, "gradlew")
        gradlew.writeText("#!/bin/sh\necho 'Gradle wrapper'")
        gradlew.setExecutable(true)
    }
}
