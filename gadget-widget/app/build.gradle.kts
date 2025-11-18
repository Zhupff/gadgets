plugins {
    id("gadget.android.application")
    id("gadget.basic") version "0"
    id("gadget.widget") version "0"
}

gadget {
    android("gadget.widget.app", "gadget.widget.app")
    enableJunitTest()
    enableViewBinding()
    dependencies {
        androidx()
    }
}

GadgetBasic {
    dependencies {
        implementation(android())
    }
}

GadgetWidget {
    dependencies {
        implementation(core())
    }
}
