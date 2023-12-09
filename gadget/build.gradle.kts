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
    api(project(":gadget:compile"))
}

gradlePlugin {
    plugins {
        register("GadgetPlugin") {
            id = "zhupf.gadget"
            implementationClass = "zhupf.gadget.GadgetPlugin"
        }
    }
}