plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.theme") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup)
}