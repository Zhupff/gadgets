plugins {
    id("gadgets.jvm")
}

script {
    configuration {
        configure()
    }
    publication {
        publish()
    }
}

dependencies {
    implementation(libs.kotlin.ksp.api)
    implementation(libs.squareup.kotlinpoet)
    implementation(project(":gadget-widget:widget-annotation"))
    implementation(project(":gadget-basic:basic-compile"))
}