plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.logger") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup)
}