plugins {
    id("com.android.application")
    id("gadget.theme")
}

android {
    namespace = "gadget.theme.app.night"
    compileSdk = 35
    defaultConfig {
        minSdk = 32
        applicationId = "gadget.theme.app.night"
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
    inject()
}
