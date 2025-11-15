import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class GadgetGradlePlugin : GadgetJvmPlugin() {

    override fun apply(target: Project) {
        super.apply(target)
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
    }
}