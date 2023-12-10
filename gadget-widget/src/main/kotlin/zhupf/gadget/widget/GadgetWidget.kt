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
}