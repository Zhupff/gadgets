plugins {
    id("gadget.android.library")
}

gadget {
    android("gadget.theme.core")
    publish()
    dependencies {
        androidx()
    }
}
