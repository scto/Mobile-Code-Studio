plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    // Standard Java/Kotlin dependencies
    implementation("javax.inject:javax.inject:1") // Required for @Inject annotations if used in pure Java/Kotlin modules
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
