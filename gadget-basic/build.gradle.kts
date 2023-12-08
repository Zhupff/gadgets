plugins {
    id("zhupf.gadget.jvm")
}

gadget {
    configuration {
        publish()
    }
}

dependencies {
    compileOnly(project(":gadget"))
}