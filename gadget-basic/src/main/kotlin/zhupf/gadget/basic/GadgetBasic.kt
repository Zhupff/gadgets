package zhupf.gadget.basic

import zhupf.gadget.Gadget
import zhupf.gadget.GadgetName

@GadgetName("Basic")
class GadgetBasic : Gadget() {

    fun jvm(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, "${GadgetBasicPublication.GROUP}:basicJvm:${GadgetBasicPublication.VERSION}")
    }

    fun android(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, "${GadgetBasicPublication.GROUP}:basicAndroid:${GadgetBasicPublication.VERSION}")
    }
}