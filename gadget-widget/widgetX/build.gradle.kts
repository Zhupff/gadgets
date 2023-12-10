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
    compileOnly(libs.androidx.core.ktx)
    implementation(project(":gadget-widget:widgetAnnotation"))
}