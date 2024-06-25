import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class Script : Plugin<Project>, MutableMap<Any, Any> by HashMap() {

    val project: Project; get() = this[Project::class.java] as Project

    override fun apply(target: Project) {
        clear()
        this[Project::class.java] = target
        if (System.getenv("JITPACK").toBoolean()) {
            target.project.group = "${System.getenv("GROUP")}.${System.getenv("ARTIFACT")}"
            target.project.version = System.getenv("VERSION")
        } else {
            target.project.group = "zhupff.gadgets"
            target.project.version = "0"
        }
    }
}


class ApplicationScript : Script() {

    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("com.android.application")
        target.pluginManager.apply("org.jetbrains.kotlin.android")
        target.extensions.add(ApplicationScript::class.java, "script", this)
    }
}


class LibraryScript : Script() {

    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("com.android.library")
        target.pluginManager.apply("org.jetbrains.kotlin.android")
        target.extensions.add(LibraryScript::class.java, "script", this)
    }
}


class JvmScript : Script() {

    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("org.jetbrains.kotlin.jvm")
        target.pluginManager.apply("java-library")
        target.pluginManager.apply("groovy")
        target.extensions.add(JvmScript::class.java, "script", this)
    }
}