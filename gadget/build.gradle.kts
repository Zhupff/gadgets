plugins {
    id("zhupf.gadget.jvm")
    `kotlin-dsl`
}

gadget {
    configuration {
        publish()
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(libs.android.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("GadgetPlugin") {
            id = "zhupf.gadget"
            implementationClass = "zhupf.gadget.GadgetPlugin"
        }
    }
}