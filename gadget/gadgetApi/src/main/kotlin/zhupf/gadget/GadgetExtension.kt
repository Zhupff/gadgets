package zhupf.gadget

import groovy.lang.Closure
import groovy.lang.GroovyClassLoader
import org.gradle.api.Project
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap

class GadgetExtension(
    val project: Project,
): MutableMap<String, GadgetDelegate> by ConcurrentHashMap(4) {

    init {
        ServiceLoader.load(GadgetDelegate::class.java).forEach { gadgetDelegate ->
            gadgetDelegate.gadgetEx = this
            put(gadgetDelegate.name, gadgetDelegate)
        }
    }

    fun compose(closure: Closure<*>) {
        if (project.buildscript.sourceFile?.name?.endsWith(".kts") == true) {
            throw IllegalStateException("This method only for groovy script")
        }

        val code = StringBuilder()
            .appendLine("package zhupf.gadget")
            .appendLine("class GadgetCompose {")
            .appendLine("  final GadgetExtension gadgetEx")
            .appendLine("  GadgetCompose(GadgetExtension gadgetEx) {")
            .appendLine("    this.gadgetEx = gadgetEx")
            .appendLine("  }")
        forEach { (name, gadgetDelegate) -> code
            .appendLine("  def ${name}(closure) {")
            .appendLine("    gadgetEx.get(\"${name}\").closure(closure)")
            .appendLine("  }")
        }
        code.appendLine("}")

        val gadgetCompose = GroovyClassLoader()
            .parseClass(code.toString())
            .getConstructor(GadgetExtension::class.java)
            .newInstance(this)
        closure.delegate = gadgetCompose
        closure.call()
    }
}