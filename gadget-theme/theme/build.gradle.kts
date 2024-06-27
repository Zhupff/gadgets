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
    implementation(gvc.androidx.appcompat)
    implementation(project(":gadget-theme:mdc"))
}