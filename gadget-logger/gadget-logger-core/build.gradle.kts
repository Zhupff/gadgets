plugins {
    id("gadget.android.library")
}

gadget {
    android("gadget.logger.core")
    publish()
    dependencies {
        androidx()
    }
}
