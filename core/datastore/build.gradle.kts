plugins {
    id("notes.android.library")
    id("notes.android.hilt")
}

android {
    namespace = "com.sitharaj.notes.core.datastore"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)
}
