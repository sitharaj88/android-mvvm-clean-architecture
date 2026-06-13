import com.sitharaj.notes.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                config.setFrom("$rootDir/detekt.yml")
            }
            dependencies {
                add("detektPlugins", libs.findLibrary("detekt-compose-rules").get())
            }
        }
    }
}
