plugins {
    id("gadget.jvm")
}

gadget {
    publish()
}

dependencies {
    implementation(project(":gadget-basic:gadget-basic-compile"))
    implementation(project(":gadget-widget:gadget-widget-annotation"))
}
