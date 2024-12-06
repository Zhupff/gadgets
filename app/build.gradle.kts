import zhupff.gadgets.basic.Basic
import zhupff.gadgets.theme.Theme

plugins {
    id("zhupff.gadgets.application")
    alias(gvc.plugins.kotlin.kapt)
}

gadgets {
    configure("zhupff.gadgets.theme") {
        sourceSets {
            getByName("main") {
                assets.srcDir("src/theme/assets")
                res.srcDir("src/theme/res")
                java.srcDir("src/theme/kotlin")
            }
        }
    }
    Basic {
        android()
    }
    Theme {
        mdc()
        theme()
        themeMerge()
    }
}

dependencies {
    implementation(gvc.autoservice.annotation)
    kapt(gvc.autoservice.processor)
    implementation(gvc.androidx.constraintlayout)
    implementation(gvc.androidx.recyclerview)
    implementation(gvc.android.material)
}