import zhupff.gadgets.theme.Theme

plugins {
    id("com.android.application")
    id("zhupff.gadgets")
}

android {
    namespace = "zhupff.gadgets.theme.diy"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        applicationId = "zhupff.gadgets.theme.diy"
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

gadgets {
    Theme {
        themePack()
    }
}