plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.basic.android") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    compileOnly(libs.androidx.startup)
}