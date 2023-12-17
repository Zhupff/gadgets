plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.widget.x") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(project(":gadget-widget:widgetAnnotation"))
}