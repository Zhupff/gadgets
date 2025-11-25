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
    ":version-catalog",
)

include(
    ":gadget-basic",
//    ":gadget-basic:app",
    ":gadget-basic:gadget-basic-android",
    ":gadget-basic:gadget-basic-compile",
    ":gadget-basic:gadget-basic-jvm",
    ":gadget-basic:gadget-basic-res",
)

include(
    ":gadget-theme",
//    ":gadget-theme:app", ":gadget-theme:app:theme-night", ":gadget-theme:app:theme-diy",
    ":gadget-theme:gadget-theme-core",
    ":gadget-theme:gadget-theme-scheme",
)

include(
    ":gadget-widget",
//    ":gadget-widget:app",
    ":gadget-widget:gadget-widget-annotation",
    ":gadget-widget:gadget-widget-compile",
    ":gadget-widget:gadget-widget-core",
)
