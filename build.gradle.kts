buildscript {
    dependencies {
//        classpath("zhupff.gadgets:gadget-basic:0")
//        classpath("zhupff.gadgets:gadget-theme:0")
//        classpath("zhupff.gadgets:theme-plugin:0")
        classpath("com.github.Zhupff.gadgets:api:9a74b030a8")
        classpath("com.github.Zhupff.gadgets:gadget-basic:9a74b030a8")
        classpath("com.github.Zhupff.gadgets:gadget-theme:9a74b030a8")
        classpath("com.github.Zhupff.gadgets:theme-plugin:9a74b030a8")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false

//    id("zhupff.gadgets") version "0" apply false
//    id("zhupff.gadgets.application") version "0" apply false
}