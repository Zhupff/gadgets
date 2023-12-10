import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.concurrent.ConcurrentHashMap

abstract class Gadget : Plugin<Project>, MutableMap<Any, Any> by ConcurrentHashMap(4) {

    val project: Project; get() = get(Project::class.java) as Project

    val configuration: Configuration?; get() = get(Configuration::class.java) as? Configuration

    val publication: Publication?; get() = get(Publication::class.java) as? Publication

    val dependency: Dependency?; get() = get(Dependency::class.java) as? Dependency

    override fun apply(target: Project) {
        println("$target apply $this")
        clear()
        put(Project::class.java, target)
        if (System.getenv("JITPACK").toBoolean()) {
            project.group = "${System.getenv("GROUP")}.${System.getenv("ARTIFACT")}"
            project.version = System.getenv("VERSION")
        } else {
            project.group = "zhupf.gadgets"
            project.version = "0"
        }
    }
}


class GadgetApplication : Gadget() {
    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("com.android.application")
        target.pluginManager.apply("org.jetbrains.kotlin.android")
        target.extensions.add(GadgetApplication::class.java, "gadget", this)
    }
}


class GadgetAndroid : Gadget() {
    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("com.android.library")
        target.pluginManager.apply("org.jetbrains.kotlin.android")
        target.extensions.add(GadgetAndroid::class.java, "gadget", this)
    }
}


class GadgetJvm : Gadget() {
    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("org.jetbrains.kotlin.jvm")
        target.pluginManager.apply("java-library")
        target.pluginManager.apply("groovy")
        target.extensions.add(GadgetJvm::class.java, "gadget", this)
    }
}