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
    compileOnly(project(":gadget:gadgetApi"))
    implementation(libs.squareup.kotlinpoet)
}