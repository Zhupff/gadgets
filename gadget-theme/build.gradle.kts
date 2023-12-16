plugins {
    id("zhupf.gadget.jvm")
}

gadget {
    configuration {
        configure()
    }
    publication {
        publish()
    }
    dependency {
        gadget()
    }
}

dependencies {
    implementation(project(":gadget-theme:themePlugin"))
}