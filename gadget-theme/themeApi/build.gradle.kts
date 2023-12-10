plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.themeApi") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    compileOnly(libs.androidx.startup)
}