plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.widget.dsl") {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.startup)
    implementation(project(":gadget-widget:widgetAnnotation"))
    implementation(project(":gadget-widget:widgetCommon"))
}