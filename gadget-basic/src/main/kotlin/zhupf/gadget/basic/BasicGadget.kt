package zhupf.gadget.basic

import zhupf.gadget.Gadget
import zhupf.gadget.GadgetExtension

private const val NAME = "Basic"

class BasicGadget : Gadget(NAME) {
}

fun GadgetExtension.Basic(closure: BasicGadget.() -> Unit = {}) {
    (gadgets[NAME] as BasicGadget).apply {
        beforeClosure(this@Basic)
        closure(this)
        afterClosure(this@Basic)
    }
}