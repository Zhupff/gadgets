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
    api(project(":gadget-widget:widget-annotation"))
}