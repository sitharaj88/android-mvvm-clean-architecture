plugins {
    id("notes.jvm.library")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    // Error-mapping helpers translate Retrofit/serialization failures into AppError.
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
}
