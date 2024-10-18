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
    dependency {
        gadget()
    }
}

dependencies {
    compileOnly(project(":gadget-transform:transform"))
    compileOnly(libs.android.gradle.v4)
}