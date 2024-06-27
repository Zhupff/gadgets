import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class Dependency<S : Script> internal constructor(
    val script: S,
) {

    init {
        assert(script[Dependency::class.java] == null)
        script[Dependency::class.java] = this
    }

    private val project: Project = script.project

    private fun project(name: String): Project = project.rootProject.findProject(name)!!

    fun gadget() {
        project.pluginManager.apply("org.jetbrains.kotlin.kapt")
        project.extensions.getByType(KaptExtension::class.java).arguments {
            arg("GROUP", project.group)
            arg("VERSION", project.version)
        }
        project.dependencies.apply {
            add("compileOnly", project.dependencies.gradleApi())
            add("compileOnly", project(":api"))
            add("kapt", project(":api"))
        }
    }
}



fun <S : Script> S.dependency(
    closure: Dependency<S>.() -> Unit = {},
) {
    Dependency(this).closure()
}