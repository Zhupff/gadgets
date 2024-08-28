plugins {
    id("gadgets.library")
}

script {
    configuration("zhupff.gadgets.theme.dsl") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.appcompat)
    api(project(":gadget-theme:theme"))
    api(project(":gadget-theme:theme-annotation"))
}