package gadget.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

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



    protected val Project.isAndroidApplication: Boolean
        get() = pluginManager.hasPlugin("com.android.application")

    protected val Project.isAndroidLibrary: Boolean
        get() = pluginManager.hasPlugin("com.android.library")

    fun Project.getBuildOutputDir(variant: String): File = this.buildDir.resolve("gadget").resolve(variant)
    fun Project.getBuildAssetsDir(variant: String): File = getBuildOutputDir(variant).resolve("assets")
}