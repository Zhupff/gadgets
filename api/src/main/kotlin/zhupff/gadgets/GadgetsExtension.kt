package zhupff.gadgets

import groovy.lang.Closure
import groovy.lang.GroovyClassLoader
import org.gradle.api.Project
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap

class GadgetsExtension(
    val project: Project,
): MutableMap<String, GadgetDelegate> by ConcurrentHashMap() {

    init {
        ServiceLoader.load(GadgetDelegate::class.java).forEach { gadgetDelegate ->
            gadgetDelegate.gadgetsEx = this
            put(gadgetDelegate.name, gadgetDelegate)
        }
    }

    fun compose(closure: Closure<*>) {
        if (project.buildscript.sourceFile?.name?.endsWith(".kts") == true) {
            throw IllegalStateException("This method only for groovy script")
        }

        val code = StringBuilder()
            .appendLine("package zhupff.gadgets")
            .appendLine("class GadgetsCompose {")
            .appendLine("  final GadgetsExtension gadgetsEx")
            .appendLine("  GadgetsCompose(GadgetsExtension gadgetsEx) {")
            .appendLine("    this.gadgetsEx = gadgetsEx")
            .appendLine("  }")
        forEach { (name, gadgetDelegate) -> code
            .appendLine("  def ${name}(closure) {")
            .appendLine("    gadgetsEx.get(\"${name}\").closure(closure)")
            .appendLine("  }")
        }
        code.appendLine("}")

        val gadgetCompose = GroovyClassLoader()
            .parseClass(code.toString())
            .getConstructor(GadgetsExtension::class.java)
            .newInstance(this)
        closure.delegate = gadgetCompose
        closure.call()
    }
}