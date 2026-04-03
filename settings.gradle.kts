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

include(":core:common")
include(":core:ui")
include(":core:utils")
include(":data")
include(":domain")

include(":feature:dashboard")
include(":feature:debug")
include(":feature:editor")
include(":feature:onboarding")
include(":feature:settings")
include(":feature:setup")
include(":feature:terminal")

include(":termux")
include(":termux:application")
include(":termux:emulator")
include(":termux:shared")
include(":termux:view")