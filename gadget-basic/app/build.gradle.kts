plugins {
    id("gadget.android.application")
    id("gadget.basic") version "0"
}

gadget {
    android("gadget.basic.app", "gadget.basic.app")
    enableJunitTest()
    enableViewBinding()
    dependencies {
        androidx()
    }
}

GadgetBasic {
    implementation(android())
    implementation(jvm())
}
