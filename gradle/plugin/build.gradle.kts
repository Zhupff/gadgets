plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradle.v8)
    compileOnly(libs.kotlin.gradle)
}

gradlePlugin {
    plugins {
        register("GadgetGradlePlugin") {
            id = "gadget.gradle"
            implementationClass = "GadgetGradlePlugin"
        }
        register("GadgetAndroidApplicationPlugin") {
            id = "gadget.android.application"
            implementationClass = "GadgetAndroidApplicationPlugin"
        }
        register("GadgetAndroidLibraryPlugin") {
            id = "gadget.android.library"
            implementationClass = "GadgetAndroidLibraryPlugin"
        }
        register("GadgetJvmPlugin") {
            id = "gadget.jvm"
            implementationClass = "GadgetJvmPlugin"
        }
    }
}
