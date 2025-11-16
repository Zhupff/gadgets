plugins {
    id("gadget.android.library")
}

gadget {
    android("gadget.theme.scheme")
    publish()
    dependencies {
        androidx()
    }
}

dependencies {
    implementation(project(":gadget-theme:gadget-theme-core"))
}
