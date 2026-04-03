plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.scto.mcs.termux.application"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    implementation(project(":termux:emulator"))
    implementation(project(":termux:shared"))
    implementation(project(":termux:view"))

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")
}
