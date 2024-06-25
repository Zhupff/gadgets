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
        register("Gadgets") {
            id = "zhupff.gadgets"
            implementationClass = "zhupff.gadgets.Gadgets"
        }
    }
}