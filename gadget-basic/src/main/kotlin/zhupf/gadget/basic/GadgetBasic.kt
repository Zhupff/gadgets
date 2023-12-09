package zhupf.gadget.basic

import zhupf.gadget.Gadget
import zhupf.gadget.GadgetName

@GadgetName("Basic")
class GadgetBasic : Gadget() {

    fun jvm(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetBasicPublication.dependency("basicJvm"))
    }

    fun android(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetBasicPublication.dependency("basicAndroid"))
    }
}