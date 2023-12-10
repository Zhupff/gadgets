package zhupf.gadget.theme

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Theme")
class GadgetTheme : GadgetDelegate() {

    fun api(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetThemePublication.dependency("themeApi"))
    }
}