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
    implementation(libs.androidx.startup)
}