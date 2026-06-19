import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt.plugin)
    jacoco
}

android {
    namespace = "com.sitharaj.notes.data"
    compileSdk = 36

    flavorDimensions += "environment"

    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
        buildConfig = true
    }

    productFlavors {
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:3000/\"")
            buildConfigField("String", "OAUTH_CLIENT_ID", "\"notes-dev-client\"")
            buildConfigField("String", "OAUTH_CLIENT_SECRET", "\"dev-secret\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
            buildConfigField("String", "OAUTH_CLIENT_ID", "\"notes-client\"")
            buildConfigField("String", "OAUTH_CLIENT_SECRET", "\"\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Retrofit / OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.coroutine.adapter)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Encrypted token storage
    implementation(libs.androidx.security.crypto)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Unit tests (data-layer tests live here; they see the module's Room/Retrofit/Work deps)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.core.ktx)
    testImplementation(libs.cucumber.junit)
    testImplementation(libs.cucumber.java)

    detektPlugins(libs.detekt.compose.rules)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/detekt.yml")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.withType<Test>().configureEach {
    extensions.findByType(JacocoTaskExtension::class.java)?.apply {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDevDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val excludeFilter = listOf(
        "**/di/**",
        "**/*_Factory.class",
        "**/*_Impl.class",
        "**/Hilt_*.class",
        "**/*_HiltModules*.*",
        "**/*_MembersInjector.class"
    )
    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/devDebug")) { exclude(excludeFilter) }
    )
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(
        fileTree(layout.buildDirectory) { include("jacoco/testDevDebugUnitTest.exec") }
    )
}
