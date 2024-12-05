package zhupff.gadgets.blur

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Blur")
class GadgetBlur : GadgetDelegate() {

    fun blur(method: String = "implementation") {
        gadgets.project.dependencies.add(method, GadgetBlurPublication.dependency("blur"))
    }
}