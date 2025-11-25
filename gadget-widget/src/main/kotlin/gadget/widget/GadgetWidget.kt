package gadget.widget

import gadget.gradle.Gadget
import gadget.gradle.GadgetEx

@GadgetEx
class GadgetWidget : Gadget() {

    fun annotation(): String = dependency("gadget-widget-annotation")

    fun compile(): String = dependency("gadget-widget-compile")

    fun core(): String = dependency("gadget-widget-core")
}