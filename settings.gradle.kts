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
        /*
        maven {
            url = uri("https://jitpack.io")
        }
        */
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
}

rootProject.name = "Mobile-Code-Studio"

include(":app")

include(":core:common")
include(":core:ui")
include(":core:utils")
include(":core:data")
include(":core:domain")
include(":core:resources")

include(":feature:dashboard")
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
