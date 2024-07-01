plugins {
    id("gadgets.application")
}

script {
    configuration("zhupff.gadgets.qrcode.test") {
        configure()
    }
}

dependencies {
    implementation(gvc.androidx.constraintlayout)
    implementation(gvc.androidx.recyclerview)
    implementation(project(":gadget-qrcode:qrcode"))
    implementation(project(":gadget-logger:logger"))
}