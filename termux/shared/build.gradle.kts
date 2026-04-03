plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.scto.mcs.termux.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    // Add common dependencies here if needed
}
