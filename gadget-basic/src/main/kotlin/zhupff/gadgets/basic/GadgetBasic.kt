package zhupff.gadgets.basic

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Basic")
class GadgetBasic : GadgetDelegate() {

    fun android(method: String = "implementation") {
        gadgets.project.dependencies.add(method, GadgetBasicPublication.dependency("basic-android"))
    }

    fun jvm(method: String = "implementation") {
        gadgets.project.dependencies.add(method, GadgetBasicPublication.dependency("basic-jvm"))
    }
}