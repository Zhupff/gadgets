plugins {
    id("zhupf.gadget.jvm")
    `kotlin-dsl`
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
    compileOnly(gradleApi())
    compileOnly(libs.android.gradle.plugin)
    implementation(project(":gadget:gadgetApi"))
}

gradlePlugin {
    plugins {
        register("Gadgets") {
            id = "zhupf.gadgets"
            implementationClass = "zhupf.gadget.Gadgets"
        }
    }
}