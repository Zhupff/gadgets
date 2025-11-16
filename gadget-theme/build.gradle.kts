plugins {
    id("gadget.gradle")
}

gadget {
    register("gadget.theme", "gadget.theme.GadgetTheme")
}

dependencies {
    compileOnly(libs.android.gradle.v8)
}
