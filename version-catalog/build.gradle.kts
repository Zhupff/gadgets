plugins {
    id("gadget.jvm")
    id("version-catalog")
    id("maven-publish")
}

catalog {
    versionCatalog {
        from(rootProject.files("./gradle/libs.versions.toml"))
    }
}

afterEvaluate {
    publishing {
        repositories {
            mavenLocal()
        }
        publications {
            create("MavenLocalPublication", MavenPublication::class.java) {
                from(components.getByName("versionCatalog"))
            }
        }
    }
}
