import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
    jacoco
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.sitharaj.notes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sitharaj.notes"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            // let Robolectric see Android resources
            isIncludeAndroidResources = true

            // configure every unit‐test task (test, testDebugUnitTest, etc.)
            all {
                // 'it' is the Test task here
                it.extensions.configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    // write the exec file under build/jacoco/<taskName>.exec
                    setDestinationFile(layout.buildDirectory
                        .file("jacoco/${it.name}.exec")
                        .get()
                        .asFile)
                }


            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.coroutine.adapter)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    // Retrofit
    implementation(libs.retrofit)
    // Lifecycle (ViewModel, LiveData)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.hilt.navigation.compose)

    // Testing
    testImplementation(libs.robolectric)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockito.kotlin)

    testImplementation(libs.androidx.core.testing)

}

detekt {
    config = files("$rootDir/detekt.yml")
    buildUponDefaultConfig = true
}

// JaCoCo configuration
plugins.withId("jacoco") {
    extensions.configure<JacocoPluginExtension> {
        toolVersion = "0.8.11"
    }
}

tasks.withType<Test>().configureEach {
    useJUnit()
    finalizedBy("jacocoTestReport")
    this.extensions.findByType(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class.java)?.apply {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

// JaCoCo configuration for Android
apply(plugin = "jacoco")

tasks.register<JacocoReport>("jacocoTestReport") {
    // make sure we run both plain and variant tests
    dependsOn("testDebugUnitTest", "test")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    // exclude what you don’t want
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
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*_MembersInjector.class",
        "**/AutoValue_*.class",
        "**/*_HiltModules.*",
        "**/*_HiltComponents.*",
        "**/presentation/ui/**"
    )
    // your class directories stay the same
    val kotlinDebugTree = fileTree("$buildDir/tmp/kotlin-classes/debug") { exclude(fileFilter) }
    val javaDebugTree   = fileTree("$buildDir/intermediates/javac/debug/classes") { exclude(fileFilter) }
    classDirectories.setFrom(files(kotlinDebugTree, javaDebugTree))

    // point source roots
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    // **grab every exec** under build/jacoco
    executionData.setFrom(fileTree("$buildDir/jacoco") {
        include("*.exec")
    })
}
