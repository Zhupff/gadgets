package zhupff.gadgets.logger

import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Logger")
class GadgetLogger : GadgetDelegate() {

    fun logger(method: String = "implementation") {
        gadgets.project.dependencies.add(method, GadgetLoggerPublication.dependency("logger"))
    }
}