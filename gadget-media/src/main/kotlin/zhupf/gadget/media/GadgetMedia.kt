package zhupf.gadget.media

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Media")
class GadgetMedia : GadgetDelegate() {

    fun media(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetMediaPublication.dependency("media"))
    }
}