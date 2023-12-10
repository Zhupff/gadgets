plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.logger.api") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    compileOnly(libs.androidx.core.ktx)
    compileOnly(libs.androidx.startup)
}