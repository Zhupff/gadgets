import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class GadgetGradlePlugin : GadgetJvmPlugin() {

    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("org.gradle.kotlin.kotlin-dsl")
        target.pluginManager.apply("org.jetbrains.kotlin.kapt")
        target.extensions.getByType(KaptExtension::class.java).arguments {
            arg("GROUP", project.group.toString())
            arg("VERSION", project.version.toString())
        }
        target.dependencies {
            implementation(project(":gadget-gradle"))
            kapt(project(":gadget-gradle"))
            compileOnly(gradleApi())
        }
        publish()
    }

    fun register(id: String, cls: String) {
        this.project.extensions.getByType(GradlePluginDevelopmentExtension::class.java).let { extension ->
            extension.plugins.register(cls) {
                this.id = id
                this.implementationClass = cls
            }
        }
    }
}