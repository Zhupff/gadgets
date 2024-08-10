package zhupff.gadgets.widget

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Widget")
class GadgetWidget : GadgetDelegate() {

    fun widget(method: String = "implementation") {
        gadgetsEx.project.dependencies.add(method, GadgetWidgetPublication.dependency("widget"))
    }
}