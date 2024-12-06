pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("gvc") {
            from("zhupff.gadgets:version-catalog:0")
        }
    }
}

rootProject.name = "GadgetTheme"
include(":app")
include(":theme-dark")
include(":theme-diy")