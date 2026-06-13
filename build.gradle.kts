// Top-level build file. Plugin versions are declared here (apply false) so the build-logic
// convention plugins can apply them by id across modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt.plugin) apply false
    alias(libs.plugins.dokka.plugin) apply false
    // Applied at the root to aggregate coverage across modules.
    alias(libs.plugins.kover)
}

// Aggregate code coverage across the pure-JVM logic modules (domain + common) — where
// business rules live and coverage matters most. `./gradlew koverHtmlReport` produces a
// merged report; `koverVerify` enforces the floor below in CI.
//
// Android/KSP-heavy modules (core:data/database/network, feature:*) are intentionally not
// aggregated here: Kover 0.9.x trips over their variant graph under AGP 8.13. Their unit
// tests still run via the normal `test` tasks; coverage for them can be added once the
// Kover/AGP interaction is resolved.
dependencies {
    kover(project(":core:domain"))
}

kover {
    reports {
        verify {
            rule {
                // Domain/business-logic coverage floor; raise as coverage grows.
                minBound(50)
            }
        }
    }
}
