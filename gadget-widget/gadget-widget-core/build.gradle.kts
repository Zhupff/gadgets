plugins {
    id("gadget.android.library")
}

gadget {
    android("gadget.widget.core")
    publish()
    dependencies {
        androidx()
    }
}
