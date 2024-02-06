plugins {
    id("zhupf.gadget.jvm")
}

gadget {
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
    implementation(project(":gadget-spi:spi"))
}