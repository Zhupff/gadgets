import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.concurrent.ConcurrentHashMap

abstract class Gadget : Plugin<Project>, MutableMap<Any, Any> by ConcurrentHashMap(4) {

    val project: Project; get() = get(Project::class.java) as Project

    val configuration: Configuration?; get() = get(Configuration::class.java) as? Configuration

    val publication: Publication?; get() = get(Publication::class.java) as? Publication

    val dependency: Dependency?; get() = get(Dependency::class.java) as? Dependency

    override fun apply(target: Project) {
        clear()
        put(Project::class.java, target)
        println("$project apply $this")
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