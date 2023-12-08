package zhupf.gadget

import org.gradle.api.Project

import java.util.concurrent.ConcurrentHashMap

class GadgetExtension {

    final Project project

    final Map<String, Gadget> gadgets = new ConcurrentHashMap<>(4)

    GadgetExtension(Project target) {
        this.project = target
        ServiceLoader.load(Gadget.class).each { gadget ->
            gadgets.put(gadget.class.canonicalName, gadget)
        }
    }

    def compose(Closure closure) {
        assert !this.project.buildscript.sourceFile.name.endsWith(".kts")
        assert closure.owner.project == this.project

        StringBuilder code = new StringBuilder()
            .append("package zhupf.gadget").append('\n')
            .append("class GadgetCompose {").append('\n')
            .append("  final GadgetExtension gadget").append('\n')
            .append("  GadgetCompose(GadgetExtension gadget) {").append('\n')
            .append("    this.gadget = gadget").append('\n')
            .append("  }").append('\n')
        gadgets.each { name, gadget -> code
            .append("  def ${name}(closure) {").append('\n')
            .append("    gadget.gadgets.get(\"${name}\").closure(gadget, closure)").append('\n')
            .append("  }").append('\n')
        }
        code.append("}")

        def gadgetCompose = new GroovyClassLoader()
            .parseClass(code.toString())
            .getConstructor(Gadget.class)
            .newInstance(this)
        closure.delegate = gadgetCompose
        closure.call()
    }
}