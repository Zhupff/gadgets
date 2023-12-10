package zhupf.gadget

import org.gradle.api.Plugin
import org.gradle.api.Project

class Gadgets implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("${target} apply ${this}")
        target.extensions.add(GadgetExtension.class, "gadgets", new GadgetExtension(target))
    }
}