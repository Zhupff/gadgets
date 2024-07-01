plugins {
    id("gadgets.library")
}

script {
    configuration("zhupff.gadgets.logger") {
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