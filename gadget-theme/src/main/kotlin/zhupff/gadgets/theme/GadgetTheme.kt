package zhupff.gadgets.theme

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Theme")
class GadgetTheme : GadgetDelegate() {

    fun mdc(method: String = "implementation") {
        gadgets.project.dependencies.add(method, GadgetThemePublication.dependency("mdc"))
    }

    fun theme(method: String = "implementation") {
        gadgets.project.dependencies.add(method, GadgetThemePublication.dependency("theme"))
    }

    fun themeMerge() {
        gadgets.project.pluginManager.apply("zhupff.gadgets.theme.merge")
    }

    fun themePack() {
        gadgets.project.pluginManager.apply("zhupff.gadgets.theme.pack")
    }

    fun themeInject(prefix: String, variant: String = prefix) {
        gadgets.project.pluginManager.apply("zhupff.gadgets.theme.inject")
        val ext = gadgets.project.extensions.getByType(Class.forName("zhupff.gadgets.theme.ThemeInjectExtension")) as (Array<String>) -> Unit
        ext.invoke(arrayOf(prefix, variant))
    }
}