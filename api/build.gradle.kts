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
    implementation(libs.squareup.kotlinpoet)
}

gradlePlugin {
    plugins {
        register("GadgetsPlugin") {
            id = "zhupff.gadgets"
            implementationClass = "zhupff.gadgets.GadgetsPlugin"
        }
    }
}