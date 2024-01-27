package zhupf.gadget.theme

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Theme")
class GadgetTheme : GadgetDelegate() {

    var merge: Boolean = false

    var pack: Boolean = false

    fun theme(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetThemePublication.dependency("theme"))
    }

    override fun afterClosure() {
        super.afterClosure()
        if (merge) {
            gadgetEx.project.plugins.apply(ThemeMerge::class.java)
        }
        if (pack) {
            gadgetEx.project.plugins.apply(ThemePack::class.java)
        }
    }
}