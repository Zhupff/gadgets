package zhupf.gadget.widget

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Widget")
class GadgetWidget : GadgetDelegate() {

    fun annotation(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetWidgetPublication.dependency("widgetAnnotation"))
    }

    fun x(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetWidgetPublication.dependency("widgetX"))
    }

    fun compile(method: String = "ksp") {
        when (method) {
            "ksp" -> {
                if (!gadgetEx.project.pluginManager.hasPlugin("com.google.devtools.ksp")) {
                    gadgetEx.project.pluginManager.apply("com.google.devtools.ksp")
                }
            }
        }
        gadgetEx.project.dependencies.add(method, GadgetWidgetPublication.dependency("widgetCompile"))
    }
}