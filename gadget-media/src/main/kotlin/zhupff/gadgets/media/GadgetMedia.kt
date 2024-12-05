package zhupff.gadgets.media

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Media")
class GadgetMedia : GadgetDelegate() {

    fun media(method: String = "implementation") {
        gadgets.project.dependencies.add(method, GadgetMediaPublication.dependency("media"))
    }
}