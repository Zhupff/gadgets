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
    compileOnly(gradleApi())
    implementation(libs.squareup.kotlinpoet)
}