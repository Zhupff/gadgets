package gadget.logger

import gadget.gradle.Gadget
import gadget.gradle.GadgetEx

@GadgetEx
class GadgetLogger : Gadget() {

    fun core(): String = dependency("gadget-logger-core")
}