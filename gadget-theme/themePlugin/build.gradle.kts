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
}

gradlePlugin {
    plugins {
        register("ThemeMerge") {
            id = "zhupf.gadget.theme.merge"
            implementationClass = "zhupf.gadget.theme.ThemeMerge"
        }
        register("ThemePack") {
            id = "zhupf.gadget.theme.pack"
            implementationClass = "zhupf.gadget.theme.ThemePack"
        }
    }
}