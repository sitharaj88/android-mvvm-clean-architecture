import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

plugins {
    id("notes.android.application")
    id("notes.android.application.compose")
    id("notes.android.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jacoco.plugin)
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
            // let Robolectric see Android resources
            isIncludeAndroidResources = true

            // configure every unit-test task (test, testDebugUnitTest, etc.)
            all {
                it.extensions.configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    setDestinationFile(
                        layout.buildDirectory
                            .file("jacoco/${it.name}.exec")
                            .get()
                            .asFile
                    )
                }
            }
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.compose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.bundles.coroutines)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    implementation(libs.retrofit)
    implementation(libs.retrofit.coroutine.adapter)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.cucumber.junit)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.core.ktx)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}

// ---------------------------------------------------------------------------
// JaCoCo coverage (migrated to Kover in a later phase)
// ---------------------------------------------------------------------------
plugins.withId("jacoco") {
    extensions.configure<JacocoPluginExtension> {
        toolVersion = "0.8.11"
    }
}

tasks.withType<Test>().configureEach {
    useJUnit()
    finalizedBy("jacocoTestReport")
    extensions.findByType(JacocoTaskExtension::class.java)?.apply {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    val deps = mutableListOf("test")
    if (tasks.findByName("testDebugUnitTest") != null) {
        deps.add("testDebugUnitTest")
    }
    dependsOn(deps)

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/Hilt_*.class",
        "**/dagger/hilt/**",
        "**/hilt_aggregated_deps/**",
        "**/di/**",
        "**/Dagger*Component.class",
        "**/*_Factory.class",
        "**/*_Impl.class",
        "**/databinding/**",
        "**/views/databinding/**",
        "**/BR.*",
        "**/*_MembersInjector.class",
        "**/AutoValue_*.class",
        "**/*_HiltModules.*",
        "**/*_HiltComponents.*",
        "**/presentation/ui/**"
    )
    val kotlinDebugTree = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug").get().asFile) { exclude(fileFilter) }
    val javaDebugTree = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes").get().asFile) { exclude(fileFilter) }
    classDirectories.setFrom(files(kotlinDebugTree, javaDebugTree))

    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    executionData.setFrom(
        fileTree(layout.buildDirectory.dir("jacoco").get().asFile) {
            include("*.exec")
        }
    )
}

// ---------------------------------------------------------------------------
// Dokka v2
// ---------------------------------------------------------------------------
dokka {
    dokkaSourceSets {
        named("main") {
            moduleName.set("Notes App")
            reportUndocumented.set(false)
        }
    }
}
