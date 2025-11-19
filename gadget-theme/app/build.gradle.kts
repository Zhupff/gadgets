plugins {
    id("gadget.android.application")
    alias(libs.plugins.kotlin.kapt)
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
        implementation(jvm())
        implementation(android())
    }
}

GadgetTheme {
    merge()
    dependencies {
        implementation(core())
        implementation(scheme())
    }
}

GadgetWidget {
    dependencies {
        implementation(core())
    }
}

dependencies {
    implementation(project(":gadget-basic:gadget-basic-res"))
    implementation(libs.android.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.autoservice.annotation)
    kapt(libs.autoservice.processor)
}
