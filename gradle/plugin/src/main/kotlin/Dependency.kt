import org.gradle.api.Project

class Dependency(
    val gadget: Gadget,
) {
    init {
        if (gadget[Dependency::class.java] != null) throw IllegalStateException("Dependency already set")
        gadget[Dependency::class.java] = this
    }

    fun compile() {
        gadget.project.pluginManager.apply("org.jetbrains.kotlin.kapt")
        gadget.project.dependencies.add("compileOnly", project(":gadget"))
        gadget.project.dependencies.add("kapt", project(":gadget:compile"))
    }

    private fun project(name: String): Project = gadget.project.rootProject.findProject(name)!!
}

fun Gadget.dependency(closure: Dependency.() -> Unit = {}) {
    Dependency(this).closure()
}