pluginManagement {
    repositories {
        google()
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
rootProject.name = "MobileCodeStudio"
include(":app")
include(":core:ui")
include(":core:data")
include(":feature:editor")
include(":feature:terminal")
include(":feature:file-browser")
include(":feature:dashboard")
include(":feature:onboarding")
include(":feature:setup")
include(":feature:settings")
include(":domain:editor")
include(":domain:terminal")
include(":domain:file-browser")
include(":domain:dashboard")
include(":data:editor")
include(":data:terminal")
include(":data:file-browser")
