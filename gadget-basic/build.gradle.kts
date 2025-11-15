plugins {
    id("gadget.gradle")
    `kotlin-dsl`
}

gadget {
    publish()
}

gradlePlugin {
    plugins {
        register("GadgetBasic") {
            id = "gadget.basic"
            implementationClass = "gadget.basic.GadgetBasic"
        }
    }
}
