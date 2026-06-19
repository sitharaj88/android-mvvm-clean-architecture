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
    namespace = "com.sitharaj.notes.core"
    compileSdk = 36

    flavorDimensions += "environment"

    defaultConfig {
        minSdk = 24
    }

    productFlavors {
        create("dev") { dimension = "environment" }
        create("prod") { dimension = "environment" }
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
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    // Hilt — :core declares pluggable DI extension points (multibindings) consumed app-wide.
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

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
