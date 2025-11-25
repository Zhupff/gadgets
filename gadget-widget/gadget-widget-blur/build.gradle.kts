plugins {
    id("gadget.android.library")
}

gadget {
    android("gadget.widget.blur")
    publish()
    dependencies {
        androidx()
    }
}
