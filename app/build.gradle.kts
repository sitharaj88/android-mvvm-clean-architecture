plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
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
        unitTests.isIncludeAndroidResources = true
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
    (this as org.gradle.api.tasks.testing.Test).extensions.findByType(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class.java)?.apply {
        setIncludeNoLocationClasses(true)
    }
}

// JaCoCo configuration for Android
apply(plugin = "jacoco")

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    group = "Reporting"
    description = "Generate Jacoco coverage reports after running tests."
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
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*_MembersInjector.class",
        "**/AutoValue_*.class",
        "**/*_HiltModules.*",
        "**/*_HiltComponents.*"
    )
    val kotlinDebugTree = fileTree("$buildDir/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val javaDebugTree = fileTree("$buildDir/intermediates/javac/debug/classes") {
        exclude(fileFilter)
    }
    val mainJavaSrc = "src/main/java"
    val mainKotlinSrc = "src/main/kotlin"
    classDirectories.setFrom(files(kotlinDebugTree, javaDebugTree))
    sourceDirectories.setFrom(files(mainJavaSrc, mainKotlinSrc))
    executionData.setFrom(files("$buildDir/jacoco/testDebugUnitTest.exec").filter { it.exists() })
    doFirst {
        println("Jacoco classDirs: " + classDirectories.files)
        println("Jacoco sourceDirs: " + sourceDirectories.files)
        println("Jacoco execData: " + executionData.files)
    }
}
