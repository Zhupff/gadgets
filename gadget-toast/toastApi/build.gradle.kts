plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.toast.api") {
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