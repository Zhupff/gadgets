plugins {
    id("gadget.jvm")
}

gadget {
    publish()
}

dependencies {
    api(libs.kotlin.ksp.api)
    api(libs.squareup.kotlinpoet)
}
