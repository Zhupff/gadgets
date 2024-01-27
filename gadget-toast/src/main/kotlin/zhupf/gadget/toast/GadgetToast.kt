package zhupf.gadget.toast

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Toast")
class GadgetToast : GadgetDelegate() {

    fun toast(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetToastPublication.dependency("toast"))
    }
}