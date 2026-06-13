import com.sitharaj.notes.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention for `feature:*` modules: an Android Compose library with Hilt + the shared
 * lifecycle/navigation dependencies every feature needs. Core-module dependencies
 * (`:core:domain`, `:core:ui`, `:core:designsystem`) are wired in once those modules exist.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("notes.android.library")
                apply("notes.android.library.compose")
                apply("notes.android.hilt")
            }
            dependencies {
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-ktx").get())
                add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
                add("implementation", libs.findBundle("compose").get())
            }
        }
    }
}
