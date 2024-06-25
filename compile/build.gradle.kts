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
    compileOnly(project(":api"))
    implementation(gvc.squareup.kotlinpoet)
}