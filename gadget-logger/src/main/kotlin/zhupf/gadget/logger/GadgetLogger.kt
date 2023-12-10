package zhupf.gadget.logger

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Logger")
class GadgetLogger : GadgetDelegate() {

    fun api(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetLoggerPublication.dependency("loggerApi"))
    }
}