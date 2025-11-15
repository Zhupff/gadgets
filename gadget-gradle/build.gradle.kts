plugins {
    id("gadget.jvm")
    `kotlin-dsl`
}

gadget {
    publish()
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.squareup.kotlinpoet)
}
