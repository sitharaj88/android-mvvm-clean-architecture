plugins {
    id("notes.android.library")
    id("notes.android.hilt")
}

android {
    namespace = "com.sitharaj.notes.core.network"
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.coroutine.adapter)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.coroutines.core)
}
