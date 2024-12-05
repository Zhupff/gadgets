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
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.squareup.kotlinpoet)
}

gradlePlugin {
    plugins {
        register("zhupff.gadgets.Gadgets") {
            id = "zhupff.gadgets"
            implementationClass = "zhupff.gadgets.Gadgets"
        }
        register("zhupff.gadgets.GadgetsAndroid") {
            id = "zhupff.gadgets.android"
            implementationClass = "zhupff.gadgets.GadgetsAndroid"
        }
        register("zhupff.gadgets.GadgetsApplication") {
            id = "zhupff.gadgets.application"
            implementationClass = "zhupff.gadgets.GadgetsApplication"
        }
        register("zhupff.gadgets.GadgetsJVM") {
            id = "zhupff.gadgets.jvm"
            implementationClass = "zhupff.gadgets.GadgetsJVM"
        }
    }
}