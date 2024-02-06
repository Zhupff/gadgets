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
    ":gadget-spi",
    ":gadget-spi:spi",
    ":gadget-spi:spiCompile",
)
include(
    ":gadget-theme",
    ":gadget-theme:theme",
    ":gadget-theme:themePlugin",
)
include(
    ":gadget-toast",
    ":gadget-toast:toast",
)
include(
    ":gadget-widget",
    ":gadget-widget:widget",
    ":gadget-widget:widgetAnnotation",
    ":gadget-widget:widgetCompile",
    ":gadget-widget:widgetDsl",
)