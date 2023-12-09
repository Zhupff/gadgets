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
include(":gadget", ":gadget:compile")
include(":gadget-basic", ":gadget-basic:basicJvm", ":gadget-basic:basicAndroid")