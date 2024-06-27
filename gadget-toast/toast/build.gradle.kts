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
    implementation(gvc.androidx.core.ktx)
    implementation(gvc.androidx.startup)
}