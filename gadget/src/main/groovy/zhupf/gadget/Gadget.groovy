package zhupf.gadget

abstract class Gadget {

    final String name

    Gadget() {
        this(getClass().simpleName)
    }

    Gadget(String name) {
        this.name = name
    }

    void beforeClosure(GadgetExtension gadget) {}

    void afterClosure(GadgetExtension gadget) {}

    final void closure(GadgetExtension gadget, Closure closure) {
        beforeClosure(gadget)
        if (closure != null) {
            closure.delegate = this
            closure.call()
        }
        afterClosure(gadget)
    }
}