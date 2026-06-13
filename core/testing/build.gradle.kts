plugins {
    id("notes.jvm.library")
}

// Shared test doubles + utilities. Consumers (JVM and Android) get JUnit, coroutines-test
// and Turbine transitively via `api`.
dependencies {
    api(project(":core:domain"))
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
}
