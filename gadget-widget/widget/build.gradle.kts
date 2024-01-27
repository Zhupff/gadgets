plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.widget") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}