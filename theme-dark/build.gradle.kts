import zhupff.gadgets.theme.Theme

plugins {
    id("com.android.application")
    id("zhupff.gadgets")
}

android {
    namespace = "zhupff.gadgets.theme.dark"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        applicationId = "zhupff.gadgets.theme.dark"
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
//        themePack()
        themeInject(prefix = "theme__", variant = "dark")
    }
}