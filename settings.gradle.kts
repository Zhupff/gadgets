pluginManagement {
    includeBuild("gradle")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "gadgets"
include(
    ":gadget",
    ":gadget:gadgetApi",
    ":gadget:gadgetCompile",
)
include(
    ":gadget-basic",
    ":gadget-basic:basicJvm",
    ":gadget-basic:basicAndroid",
)
include(
    ":gadget-logger",
    ":gadget-logger:loggerApi",
)
include(
    ":gadget-theme",
    ":gadget-theme:themeApi",
    ":gadget-theme:themePlugin",
)
include(
    ":gadget-toast",
    ":gadget-toast:toastApi",
)
include(
    ":gadget-widget",
    ":gadget-widget:widgetAnnotation",
    ":gadget-widget:widgetX",
)