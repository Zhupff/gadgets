package zhupff.gadgets

import groovy.lang.Closure

abstract class GadgetDelegate {

    val name: String = this::class.java.getAnnotation(GadgetName::class.java).value

    lateinit var gadgetsEx: Gadgets

    open fun beforeClosure() {}

    open fun afterClosure() {}

    fun closure(closure: Closure<*>?) {
        beforeClosure()
        if (closure != null) {
            closure.delegate = this
            closure.call()
        }
        afterClosure()
    }
}