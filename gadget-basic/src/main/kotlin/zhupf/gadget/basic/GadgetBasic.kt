package zhupf.gadget.basic

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Basic")
class GadgetBasic : GadgetDelegate() {

    fun jvm(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetBasicPublication.dependency("basicJvm"))
    }

    fun android(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetBasicPublication.dependency("basicAndroid"))
    }
}