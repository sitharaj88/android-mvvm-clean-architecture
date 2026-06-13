plugins {
    id("notes.android.library")
    id("notes.android.library.compose")
}

android {
    namespace = "com.sitharaj.notes.design"

    flavorDimensions += "environment"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    productFlavors {
        create("dev") { dimension = "environment" }
        create("prod") { dimension = "environment" }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.compose)
}
