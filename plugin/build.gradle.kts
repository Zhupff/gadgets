plugins {
    id("gadgets.jvm")
    `kotlin-dsl`
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
    implementation(project(":api"))
}

gradlePlugin {
    plugins {
        register("GadgetsPlugin") {
            id = "zhupff.gadgets"
            implementationClass = "zhupff.gadgets.GadgetsPlugin"
        }
    }
}