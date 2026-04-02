pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Mobile-Code-Studio"
include(":app")
include(":core")
include(":core:ui")
include(":domain")
include(":data")
include(":feature:onboarding")
include(":feature:setup")
include(":feature:dashboard")
include(":feature:editor")
include(":feature:settings")
include(":feature:debug")
