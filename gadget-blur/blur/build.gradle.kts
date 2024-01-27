plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.blur") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.kotlin.coroutines)
}