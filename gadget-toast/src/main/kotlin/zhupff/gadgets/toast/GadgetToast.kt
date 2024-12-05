package zhupff.gadgets.toast

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Toast")
class GadgetToast : GadgetDelegate() {

    fun toast(method: String = "implementation") {
        gadgets.project.dependencies.add(method, GadgetToastPublication.dependency("toast"))
    }
}