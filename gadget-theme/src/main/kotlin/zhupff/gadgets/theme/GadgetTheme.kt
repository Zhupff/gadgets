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

    fun annotation(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetThemePublication.dependency("theme-annotation"))
    }

    fun dsl(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetThemePublication.dependency("theme-dsl"))
    }

    fun compile(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetThemePublication.dependency("theme-compile"))
    }

    fun themeMerge() {
        gadgetsEx.project.pluginManager.apply("zhupff.gadgets.theme.merge")
    }

    fun themePack() {
        gadgetsEx.project.pluginManager.apply("zhupff.gadgets.theme.pack")
    }
}