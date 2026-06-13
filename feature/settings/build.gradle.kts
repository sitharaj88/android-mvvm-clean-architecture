plugins {
    id("notes.android.feature")
}

android {
    namespace = "com.sitharaj.notes.feature.settings"
}

dependencies {
    implementation(project(":core:datastore"))
    implementation(libs.bundles.coroutines)
}
