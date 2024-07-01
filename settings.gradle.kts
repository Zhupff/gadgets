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
    versionCatalogs {
        create("gvc") {
            from(
                if (System.getenv("JITPACK").toBoolean())
                    "com.github.Zhupff:gadget-version-catalog:5f0a8b9ad5"
                else
                    "zhupff.gadget:gadget-version-catalog:0"
            )
        }
    }
}

rootProject.name = "gadgets"
include(
    ":api",
    ":plugin",
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
    ":gadget-qrcode",
    ":gadget-qrcode:qrcode",
    ":gadget-qrcode:qrcodeTest",
)

include(
    ":gadget-theme",
    ":gadget-theme:mdc",
    ":gadget-theme:theme",
    ":gadget-theme:themePlugin",
)

include(
    ":gadget-toast",
    ":gadget-toast:toast",
)