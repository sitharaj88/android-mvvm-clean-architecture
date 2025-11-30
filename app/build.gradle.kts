import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
// no-op

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt.plugin)
    alias(libs.plugins.jacoco.plugin)
    alias(libs.plugins.dokka.plugin)
}

android {
    namespace = "com.sitharaj.notes"
    compileSdk = 36
    //noinspection GradleDependency

    flavorDimensions += "environment"

    defaultConfig {
        applicationId = "com.sitharaj.notes"
        minSdk = 24
        targetSdk = 35
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

    implementation(project(":design"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
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
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.logging.interceptor.v520)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
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
    testImplementation(libs.cucumber.junit)
    testImplementation(libs.cucumber.java)
    // Not using cucumber-kotlin artifact to improve compatibility; use cucumber-java and cucumber-junit instead.
    testImplementation(libs.mockito.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockito.kotlin)

    testImplementation(libs.androidx.core.testing)

    detektPlugins(libs.detekt.compose.rules)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/detekt.yml")
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
    val deps = mutableListOf("test")
    if (tasks.findByName("testDebugUnitTest") != null) {
        deps.add("testDebugUnitTest")
    }
    dependsOn(deps)

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
    val kotlinDebugTree = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug").get().asFile) { exclude(fileFilter) }
    val javaDebugTree   = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes").get().asFile) { exclude(fileFilter) }
    classDirectories.setFrom(files(kotlinDebugTree, javaDebugTree))

    // point source roots
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    // **grab every exec** under build/jacoco
    executionData.setFrom(fileTree(layout.buildDirectory.dir("jacoco").get().asFile) {
        include("*.exec")
    })
}

// Configure Dokka v2 source sets and set a module display name
dokka {
    dokkaSourceSets {
        named("main") {
            moduleName.set("Notes App")

            // If needed, you can add external documentation links here to resolve
            // links to Kotlin stdlib and kotlinx-coroutines by adding
            // `externalDocumentationLink { url.set(URI("https://...")) }` blocks.
            // External documentation links were intentionally omitted:
            // They caused script compilation issues in the Gradle Kotlin DSL in
            // this environment. If you need external links to Kotlin stdlib or
            // kotlinx-coroutines, re-add them after verifying the Dokka plugin
            // and DSL compatibility (or use per-module sourceSet names).
            // Suppress linking warnings for some generated or internal packages
            reportUndocumented.set(false)
        }
    }
}
