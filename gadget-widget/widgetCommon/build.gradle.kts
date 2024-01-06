plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.widget.common") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}