plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.basic") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    compileOnly(libs.androidx.startup)
}