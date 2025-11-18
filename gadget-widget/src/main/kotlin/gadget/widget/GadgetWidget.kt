package gadget.widget

import gadget.gradle.Gadget
import gadget.gradle.GadgetEx

@GadgetEx
class GadgetWidget : Gadget() {

    fun core(): String = dependency("gadget-widget-core")
}