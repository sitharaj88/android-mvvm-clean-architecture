plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.sitharaj.notes.domain"
    compileSdk = 36

    flavorDimensions += "environment"

    defaultConfig {
        minSdk = 24
    }

    productFlavors {
        create("dev") { dimension = "environment" }
        create("prod") { dimension = "environment" }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Domain is pure Kotlin business logic: models, repository contracts, use cases, validators.
    // It depends only on :core (Result/AppError) and coroutines — never on data or presentation.
    implementation(project(":core"))
    implementation(libs.kotlinx.coroutines.core)
}
