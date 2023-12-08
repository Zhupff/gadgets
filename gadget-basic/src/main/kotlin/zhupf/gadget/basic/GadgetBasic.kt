package zhupf.gadget.basic

import zhupf.gadget.Gadget
import zhupf.gadget.GadgetExtension

private const val NAME = "Basic"

class GadgetBasic : Gadget(NAME) {

    fun jvm(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, "")
    }

    fun android(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, "")
    }
}

fun GadgetExtension.Basic(closure: GadgetBasic.() -> Unit = {}) {
    val gadget = gadgets[NAME] as GadgetBasic
    gadget.beforeClosure()
    closure(gadget)
    gadget.afterClosure()
}