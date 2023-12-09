plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("GadgetApplication") {
            id = "zhupf.gadget.application"
            implementationClass = "GadgetApplication"
        }
        register("GadgetAndroid") {
            id = "zhupf.gadget.android"
            implementationClass = "GadgetAndroid"
        }
        register("GadgetJvm") {
            id = "zhupf.gadget.jvm"
            implementationClass = "GadgetJvm"
        }
    }
}