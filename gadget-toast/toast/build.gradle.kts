plugins {
    id("gadgets.library")
}

script {
    configuration("zhupff.gadgets.toast") {
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