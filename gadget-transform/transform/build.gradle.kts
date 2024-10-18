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
    implementation(project(":gadget-basic:basic-print"))
    compileOnly(gradleApi())
    compileOnly(libs.android.gradle.v4)
}