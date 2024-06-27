package zhupff.gadgets.theme

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Theme")
class GadgetTheme : GadgetDelegate() {

    fun mdc(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetThemePublication.dependency("mdc"))
    }

    fun theme(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetThemePublication.dependency("theme"))
    }
}