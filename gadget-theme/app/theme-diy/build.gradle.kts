plugins {
    id("com.android.application")
    id("gadget.theme")
}

android {
    namespace = "gadget.theme.app.diy"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
        applicationId = "gadget.theme.app.diy"
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = false
        }
    }
}

GadgetTheme {
    pack()
}
