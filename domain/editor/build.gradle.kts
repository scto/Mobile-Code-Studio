plugins {
    alias(libs.plugins.jetbrains.kotlin.android)
    // No android.library plugin needed if it's a pure Kotlin module
}

android {
    namespace = "com.scto.mcs.domain.editor"
    compileSdk = 36 // Still needs this for Android specific Lint checks

    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Domain modules typically depend on nothing or other domain/core modules
    // but not presentation or data layers directly.
    implementation(libs.androidx.core.ktx) // For common utilities like Context
    testImplementation(libs.junit)
}
