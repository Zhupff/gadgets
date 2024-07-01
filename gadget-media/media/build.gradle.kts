plugins {
    id("gadgets.library")
}

script {
    configuration("zhupff.gadgets.media") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
}