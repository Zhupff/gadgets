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
}

gradlePlugin {
    plugins {
        register("ThemeMergePlugin") {
            id = "zhupff.gadgets.theme.merge"
            implementationClass = "zhupff.gadgets.theme.ThemeMergePlugin"
        }
        register("ThemePackPlugin") {
            id = "zhupff.gadgets.theme.pack"
            implementationClass = "zhupff.gadgets.theme.ThemePackPlugin"
        }
        register("ThemeInjectPlugin") {
            id = "zhupff.gadgets.theme.inject"
            implementationClass = "zhupff.gadgets.theme.ThemeInjectPlugin"
        }
    }
}