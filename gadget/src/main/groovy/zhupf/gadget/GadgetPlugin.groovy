package zhupf.gadget

import org.gradle.api.Plugin
import org.gradle.api.Project

class GadgetPlugin implements Plugin< Project> {
    @Override
    void apply(Project target) {
        println("${target} apply ${this}")
        target.extensions.add(GadgetExtension.class, "gadget", new GadgetExtension(this))
    }
}