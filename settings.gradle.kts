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
//            from("zhupff.gadgets:version-catalog:0")

            // 服了，jitpack打不出toml文件??? https://github.com/jitpack/jitpack.io/issues/5713
            from(files("./gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "GadgetTheme"
include(":app")
include(":theme-dark")
include(":theme-diy")