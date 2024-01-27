package zhupf.gadget.logger

import zhupf.gadget.GadgetDelegate
import zhupf.gadget.GadgetName

@GadgetName("Logger")
class GadgetLogger : GadgetDelegate() {

    fun logger(method: String = "implementation") {
        gadgetEx.project.dependencies.add(method, GadgetLoggerPublication.dependency("logger"))
    }
}