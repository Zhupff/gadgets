plugins {
    id("zhupf.gadget.jvm")
}

gadget {
    configuration {
        configure()
    }
}

dependencies {
    implementation(libs.squareup.kotlinpoet)
}