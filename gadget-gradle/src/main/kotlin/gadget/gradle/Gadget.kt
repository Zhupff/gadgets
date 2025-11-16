package gadget.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class Gadget : Plugin<Project> {

    protected lateinit var project: Project
        private set

    override fun apply(target: Project) {
        println("${target} apply ${this}")
        this.project = target
        val gadgetEx = this::class.java.getAnnotation(GadgetEx::class.java).value.ifBlank {
            this::class.java.simpleName
        }.replaceFirstChar { it.uppercaseChar() }
        target.extensions.add(this.javaClass, gadgetEx, this)
    }

    fun api(dependency: Any) {
        this.project.dependencies.add("api", dependency)
    }

    fun implementation(dependency: Any) {
        this.project.dependencies.add("implementation", dependency)
    }

    fun compileOnly(dependency: Any) {
        this.project.dependencies.add("compileOnly", dependency)
    }

    fun kapt(dependency: Any) {
        this.project.dependencies.add("kapt", dependency)
    }

    fun ksp(dependency: Any) {
        this.project.dependencies.add("ksp", dependency)
    }



    protected val Project.isAndroidApplication: Boolean
        get() = pluginManager.hasPlugin("com.android.application")

    protected val Project.isAndroidLibrary: Boolean
        get() = pluginManager.hasPlugin("com.android.library")
}