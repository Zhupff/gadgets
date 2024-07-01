plugins {
    id("gadgets.application")
}

script {
    configuration("zhupff.gadgets.qrcode.test") {
        configure()
    }
}

dependencies {
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(project(":gadget-qrcode:qrcode"))
    implementation(project(":gadget-logger:logger"))
}