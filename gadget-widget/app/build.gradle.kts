plugins {
    id("gadget.android.application")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.ksp)
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
        implementation(annotation())
        ksp(compile())
        implementation(core())
    }
}

dependencies {
    implementation(project(":gadget-basic:gadget-basic-res"))
}
