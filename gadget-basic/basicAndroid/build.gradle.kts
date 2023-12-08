plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.basic") {
        publish()
    }
}

dependencies {
    compileOnly(libs.androidx.startup)
}