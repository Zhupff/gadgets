package zhupf.gadget

abstract class Gadget {

    final String name = this.getClass().getAnnotation(GadgetName.class).value()

    GadgetExtension gadgetEx

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