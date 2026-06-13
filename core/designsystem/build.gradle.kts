plugins {
    id("notes.android.library")
    id("notes.android.library.compose")
}

android {
    namespace = "com.sitharaj.notes.design"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.compose)
}
