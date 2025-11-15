package gadget.basic

import gadget.gradle.Gadget
import gadget.gradle.GadgetEx

@GadgetEx
class GadgetBasic : Gadget() {

    fun android(): String = dependency("gadget-basic-android")

    fun jvm(): String = dependency("gadget-basic-jvm")
}