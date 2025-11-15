plugins {
    id("gadget.android.library")
}

gadget {
    android("gadget.basic")
    publish()
    dependencies {
        androidx()
    }
}
