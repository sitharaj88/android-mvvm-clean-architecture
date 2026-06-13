import org.gradle.api.Plugin
import org.gradle.api.Project

/** Applies Kover so the module's coverage is collected and can be aggregated at the root. */
class KoverConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("org.jetbrains.kotlinx.kover")
    }
}
