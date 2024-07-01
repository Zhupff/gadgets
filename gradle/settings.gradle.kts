pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
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
        create("libs") {
            from(files("./libs.versions.toml"))
        }
    }
}

rootProject.name = "gradle"
include(":script")