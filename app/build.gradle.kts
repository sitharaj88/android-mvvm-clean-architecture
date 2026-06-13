plugins {
    id("notes.android.application")
    id("notes.android.application.compose")
    id("notes.android.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka.plugin)
}

android {
    namespace = "com.sitharaj.notes"

    flavorDimensions += "environment"

    defaultConfig {
        applicationId = "com.sitharaj.notes"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("prod") {
            dimension = "environment"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":feature:notes"))
    implementation(project(":feature:settings"))

    // Navigation 3 — developer-owned back stack hosted in :app.
    // navigation3-ui transitively provides an AGP-8.13-compatible ViewModel-scoping
    // decorator; pinning lifecycle-viewmodel-navigation3 explicitly would drag in
    // lifecycle 2.11 (needs compileSdk 37 / AGP 9.1), so it is intentionally omitted.
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.json) // @Serializable NavKeys

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.coroutines)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}

// Multi-module API documentation. `./gradlew dokkaGenerateHtml`
dokka {
    dokkaSourceSets {
        named("main") {
            moduleName.set("Notes App")
            reportUndocumented.set(false)
        }
    }
}
