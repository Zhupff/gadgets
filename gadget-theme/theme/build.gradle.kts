plugins {
    id("gadgets.library")
}

script {
    configuration("zhupff.gadgets.theme") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.startup)
    implementation(project(":gadget-theme:mdc"))
}