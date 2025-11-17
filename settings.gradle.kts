pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
    includeBuild("gradle")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "gadgets"

include(
    ":gadget-gradle",
)

include(
    ":gadget-basic",
//    ":gadget-basic:app",
    ":gadget-basic:gadget-basic-android",
    ":gadget-basic:gadget-basic-jvm",
)

include(
    ":gadget-theme",
//    ":gadget-theme:app", ":gadget-theme:app:theme-night", ":gadget-theme:app:theme-diy",
    ":gadget-theme:gadget-theme-core",
    ":gadget-theme:gadget-theme-scheme",
)

include(
    ":gadget-widget",
)
