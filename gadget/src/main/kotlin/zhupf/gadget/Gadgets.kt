package zhupf.gadget

import org.gradle.api.Plugin
import org.gradle.api.Project

class Gadgets : Plugin<Project> {

    override fun apply(target: Project) {
        println("$target apply $this")
        target.extensions.add(GadgetExtension::class.java, "gadgets", GadgetExtension(target))
    }
}