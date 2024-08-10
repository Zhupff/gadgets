package zhupff.gadgets

import org.gradle.api.Plugin
import org.gradle.api.Project

class GadgetsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("$target apply $this")
        target.extensions.add(GadgetsExtension::class.java, "gadgets", GadgetsExtension(target))
    }
}