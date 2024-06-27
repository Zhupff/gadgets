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
    implementation(gvc.androidx.appcompat)
    implementation(gvc.androidx.startup)
}