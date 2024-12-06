buildscript {
    dependencies {
        classpath("zhupff.gadgets:gadget-basic:0")
        classpath("zhupff.gadgets:gadget-theme:0")
        classpath("zhupff.gadgets:theme-plugin:0")
    }
}
plugins {
    alias(gvc.plugins.android.application) apply false
    alias(gvc.plugins.kotlin.android) apply false
    alias(gvc.plugins.kotlin.kapt) apply false

    id("zhupff.gadgets") version "0" apply false
    id("zhupff.gadgets.application") version "0" apply false
}