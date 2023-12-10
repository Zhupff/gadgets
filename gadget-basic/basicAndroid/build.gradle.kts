plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.basicAndroid") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    compileOnly(libs.androidx.startup)
}