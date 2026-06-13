plugins {
    id("notes.android.library")
    id("notes.android.room")
    id("notes.android.hilt")
}

android {
    namespace = "com.sitharaj.notes.core.database"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
