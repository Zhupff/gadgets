package gadget.rxjava

import gadget.gradle.Gadget
import gadget.gradle.GadgetEx

@GadgetEx
class GadgetRxJava : Gadget() {

    fun core3(): String = dependency("gadget-rxjava3-core")
}