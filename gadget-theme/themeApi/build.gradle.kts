plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.theme.api") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    compileOnly(libs.androidx.appcompat)
    compileOnly(libs.androidx.core.ktx)
    compileOnly(libs.androidx.startup)
}