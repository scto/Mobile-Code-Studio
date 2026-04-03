package com.scto.mcs.core

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * EnvironmentHealthChecker: Prüft die Integrität eines VCSpace-Containers.
 */
@Singleton
class EnvironmentHealthChecker @Inject constructor(
    private val containerManager: ContainerManager
) {

    data class HealthStatus(
        val isHealthy: Boolean,
        val issues: List<String>
    )

    /**
     * Prüft den Zustand eines Containers.
     */
    fun checkHealth(containerId: String): HealthStatus {
        val containerDir = containerManager.getContainerRoot(containerId)
        val issues = mutableListOf<String>()

        if (!containerDir.exists()) {
            issues.add("Container directory does not exist")
            return HealthStatus(false, issues)
        }

        // Check for essential directories (e.g., usr, bin, home)
        val essentialDirs = listOf("usr", "bin", "home")
        essentialDirs.forEach { dirName ->
            val dir = File(containerDir, dirName)
            if (!dir.exists()) {
                issues.add("Essential directory missing: $dirName")
            }
        }

        return HealthStatus(issues.isEmpty(), issues)
    }
}
