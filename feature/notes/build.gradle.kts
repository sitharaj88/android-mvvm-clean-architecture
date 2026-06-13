plugins {
    id("notes.android.feature")
}

android {
    namespace = "com.sitharaj.notes.feature.notes"
}

dependencies {
    implementation(libs.bundles.coroutines)
}
