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
    compileOnly(gvc.android.gradle.plugin)
    compileOnly(gvc.kotlin.gradle.plugin)
    compileOnly(gvc.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        plugins {
            register("ApplicationScript") {
                id = "gadgets.application"
                implementationClass = "ApplicationScript"
            }
            register("LibraryScript") {
                id = "gadgets.library"
                implementationClass = "LibraryScript"
            }
            register("JvmScript") {
                id = "gadgets.jvm"
                implementationClass = "JvmScript"
            }
        }
    }
}