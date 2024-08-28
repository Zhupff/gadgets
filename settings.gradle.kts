pluginManagement {
    includeBuild("gradle")
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
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
    ":api",
    ":version-catalog",
)

include(
    ":gadget-basic",
    ":gadget-basic:basic-android",
    ":gadget-basic:basic-compile",
    ":gadget-basic:basic-jvm",
)

include(
    ":gadget-blur",
    ":gadget-blur:blur",
)

include(
    ":gadget-logger",
    ":gadget-logger:logger",
)

include(
    ":gadget-media",
    ":gadget-media:media",
)

include(
    ":gadget-qrcode",
    ":gadget-qrcode:qrcode",
    ":gadget-qrcode:qrcode-test",
)

include(
    ":gadget-theme",
    ":gadget-theme:mdc",
    ":gadget-theme:theme",
    ":gadget-theme:theme-annotation",
    ":gadget-theme:theme-compile",
    ":gadget-theme:theme-dsl",
    ":gadget-theme:theme-plugin",
)

include(
    ":gadget-toast",
    ":gadget-toast:toast",
)

include(
    ":gadget-widget",
    ":gadget-widget:widget",
    ":gadget-widget:widget-annotation",
    ":gadget-widget:widget-compile",
    ":gadget-widget:widget-dsl",
)