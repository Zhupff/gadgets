plugins {
    id("gadget.android.application")
    id("gadget.basic") version "0"
    id("gadget.theme") version "0"
    id("gadget.widget") version "0"
}

gadget {
    android("gadget.theme.app", "gadget.theme.app")
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

GadgetTheme {
    merge()
    dependencies {
        implementation(scheme())
    }
}

GadgetWidget {
    dependencies {
        implementation(core())
    }
}
