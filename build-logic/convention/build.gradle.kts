plugins {
    `kotlin-dsl`
}

group = "com.sitharaj.notes.buildlogic"

// Convention plugins are compiled against JDK 17 (required by AGP 8.x / Kotlin 2.x toolchains).
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

// Register each convention plugin with a stable `notes.*` id so modules can apply them
// declaratively, e.g. `plugins { id("notes.android.library") }`.
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "notes.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "notes.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "notes.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "notes.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "notes.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "notes.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "notes.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("jvmLibrary") {
            id = "notes.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("detekt") {
            id = "notes.detekt"
            implementationClass = "DetektConventionPlugin"
        }
        register("kover") {
            id = "notes.kover"
            implementationClass = "KoverConventionPlugin"
        }
    }
}
