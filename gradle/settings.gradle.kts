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

rootProject.name = "gradle"
include(":script")