// AGP 9 compiles Kotlin sources with its built-in Kotlin support,
// so no separate org.jetbrains.kotlin.android plugin is applied.
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.arafat.vangtichai"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.arafat.vangtichai"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // Signed in Android Studio (Build > Generate Signed App Bundle), so no
        // keystore path or password is stored in this file.
        release {
            isDebuggable = false
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
