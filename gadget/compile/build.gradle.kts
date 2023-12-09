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
    implementation(libs.squareup.kotlinpoet)
}