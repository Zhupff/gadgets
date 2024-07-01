plugins {
    id("gadgets.library")
}

script {
    configuration("zhupff.gadgets.basic") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.startup)
}