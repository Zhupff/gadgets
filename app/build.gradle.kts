import zhupff.gadgets.basic.Basic
import zhupff.gadgets.theme.Theme

plugins {
    id("zhupff.gadgets.application")
    alias(libs.plugins.kotlin.kapt)
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.android.material)
    implementation(libs.autoservice.annotation)
    kapt(libs.autoservice.processor)
}