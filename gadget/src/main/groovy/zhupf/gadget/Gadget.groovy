package zhupf.gadget

abstract class Gadget {

    final String name

    GadgetExtension gadgetEx

    Gadget(String name) {
        this.name = name
    }

    void beforeClosure() {}

    void afterClosure() {}

    final void closure(Closure closure) {
        beforeClosure()
        if (closure != null) {
            closure.delegate = this
            closure.call()
        }
        afterClosure()
    }
}