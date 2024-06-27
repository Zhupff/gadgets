package zhupff.gadgets.basic

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Basic")
class GadgetBasic : GadgetDelegate() {

    fun jvm(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetBasicPublication.dependency("basicJvm"))
    }

    fun android(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetBasicPublication.dependency("basicAndroid"))
    }
}