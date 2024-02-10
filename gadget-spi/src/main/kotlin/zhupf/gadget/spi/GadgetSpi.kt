package zhupf.gadget.spi

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Spi")
class GadgetSpi : GadgetDelegate() {

    fun spi(method: String) {
        gadgetEx.project.dependencies.add(method, GadgetSpiPublication.dependency("spi"))
    }

    fun compile(method: String = "ksp") {
        when (method) {
            "ksp" -> {
                if (!gadgetEx.project.pluginManager.hasPlugin("com.google.devtools.ksp")) {
                    gadgetEx.project.pluginManager.apply("com.google.devtools.ksp")
                }
            }
        }
        gadgetEx.project.dependencies.add(method, GadgetSpiPublication.dependency("spiCompile"))
    }
}