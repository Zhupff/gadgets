plugins {
    id("gadgets.library")
}

script {
    configuration("zhupff.gadgets.widget.dsl") {
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
    implementation(libs.androidx.constraintlayout)
    api(project(":gadget-widget:widget-annotation"))
}