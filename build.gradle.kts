buildscript {
    dependencies {
//        classpath("zhupff.gadgets:gadget-basic:0")
//        classpath("zhupff.gadgets:gadget-theme:0")
//        classpath("zhupff.gadgets:theme-plugin:0")
        classpath("com.github.Zhupff.gadgets:api:5e377498fd")
        classpath("com.github.Zhupff.gadgets:gadget-basic:5e377498fd")
        classpath("com.github.Zhupff.gadgets:gadget-theme:5e377498fd")
        classpath("com.github.Zhupff.gadgets:theme-plugin:5e377498fd")
    }
}
plugins {
    alias(gvc.plugins.android.application) apply false
    alias(gvc.plugins.kotlin.android) apply false
    alias(gvc.plugins.kotlin.kapt) apply false

//    id("zhupff.gadgets") version "0" apply false
//    id("zhupff.gadgets.application") version "0" apply false
}