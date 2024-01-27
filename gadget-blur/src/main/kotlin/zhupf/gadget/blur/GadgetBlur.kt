package zhupf.gadget.blur

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Blur")
class GadgetBlur : GadgetDelegate() {

    fun blur(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetBlurPublication.dependency("blur"))
    }
}