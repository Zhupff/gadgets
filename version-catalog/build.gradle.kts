plugins {
    id("gadgets.jvm")
    id("version-catalog")
    id("maven-publish")
}

script {
    configuration {
        configure()
    }
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