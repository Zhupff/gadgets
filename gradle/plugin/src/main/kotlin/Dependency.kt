import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class Dependency(
    val gadget: Gadget,
) {
    init {
        if (gadget[Dependency::class.java] != null) throw IllegalStateException("Dependency already set")
        gadget[Dependency::class.java] = this
    }

    fun gadget() {
        gadget.project.pluginManager.apply("org.jetbrains.kotlin.kapt")
        gadget.project.extensions.getByType(KaptExtension::class.java).arguments {
            arg("GROUP", gadget.project.group)
            arg("VERSION", gadget.project.version)
        }
        gadget.project.dependencies.add("compileOnly", gadget.project.dependencies.gradleApi())
        gadget.project.dependencies.add("compileOnly", project(":gadget:gadgetApi"))
        gadget.project.dependencies.add("kapt", project(":gadget:gadgetCompile"))
    }

    private fun project(name: String): Project = gadget.project.rootProject.findProject(name)!!
}

fun Gadget.dependency(closure: Dependency.() -> Unit = {}) {
    (dependency ?: Dependency(this)).closure()
}