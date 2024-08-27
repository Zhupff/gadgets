package zhupff.gadgets.widget

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Widget")
class GadgetWidget : GadgetDelegate() {

    fun widget(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetWidgetPublication.dependency("widget"))
    }

    fun annotation(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetWidgetPublication.dependency("widget-annotation"))
    }

    fun compile(method: String = "annotationProcessor") {
        gadgetsEx.project.dependencies.add(method, GadgetWidgetPublication.dependency("widget-compile"))
    }

    fun dsl(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetWidgetPublication.dependency("widget-dsl"))
    }
}