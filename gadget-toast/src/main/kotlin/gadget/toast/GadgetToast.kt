package gadget.toast

import gadget.gradle.Gadget
import gadget.gradle.GadgetEx

@GadgetEx
class GadgetToast : Gadget() {

    fun core(): String = dependency(":gadget-toast-core")
}