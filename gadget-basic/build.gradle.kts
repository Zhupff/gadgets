plugins {
    id("zhupf.gadget.jvm")
    alias(libs.plugins.kotlin.kapt)
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
    compileOnly(project(":gadget"))
    kapt(project(":gadget:compile"))
}