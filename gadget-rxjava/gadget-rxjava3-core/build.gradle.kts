plugins {
    id("gadget.jvm")
}

gadget {
    publish()
}

dependencies {
    api(libs.reactivex.rxjava3)
}
