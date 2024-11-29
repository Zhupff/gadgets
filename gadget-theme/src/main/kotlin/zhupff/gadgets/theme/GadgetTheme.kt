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

    fun themeMerge() {
        gadgetsEx.project.pluginManager.apply("zhupff.gadgets.theme.merge")
    }

    fun themePack() {
        gadgetsEx.project.pluginManager.apply("zhupff.gadgets.theme.pack")
    }

    fun themeInject(prefix: String, variant: String = prefix) {
        gadgetsEx.project.pluginManager.apply("zhupff.gadgets.theme.inject")
        val ext = gadgetsEx.project.extensions.getByType(Class.forName("zhupff.gadgets.theme.ThemeInjectExtension")) as (Array<String>) -> Unit
        ext.invoke(arrayOf(prefix, variant))
    }
}