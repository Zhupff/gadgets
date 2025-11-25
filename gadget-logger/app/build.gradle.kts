plugins {
    id("gadget.android.application")
    id("gadget.basic") version "0"
    id("gadget.logger") version "0"
}

gadget {
    android("gadget.logger.app", "gadget.logger.app")
    dependencies {
        androidx()
    }
}

GadgetBasic {
    dependencies {
        implementation(android())
    }
}

GadgetLogger {
    dependencies {
        implementation(core())
    }
}
