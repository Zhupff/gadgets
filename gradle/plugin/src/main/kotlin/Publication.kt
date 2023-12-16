import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

class Publication(
    val gadget: Gadget,
) {
    init {
        if (gadget[Publication::class.java] != null) throw IllegalStateException("Publication already set")
        gadget[Publication::class.java] = this
    }

    fun publish() {
        gadget.project.pluginManager.apply("maven-publish")
        gadget.project.afterEvaluate {
            gadget.project.extensions.configure(PublishingExtension::class.java) {
                repositories {
                    mavenLocal()
                }
                publications {
                    create("MavenLocalPublication", MavenPublication::class.java) {
                        from(
                            gadget.project.components.getByName(
                                when (gadget) {
                                    is GadgetApplication, is GadgetAndroid -> "release"
                                    is GadgetJvm -> "java"
                                    else -> throw IllegalStateException("$gadget can't publish")
                                }
                            )
                        )
                        groupId = gadget.project.group.toString()
                        artifactId = gadget.project.name
                        version = gadget.project.version.toString()
                    }
                }
            }
        }
    }
}

fun Gadget.publication(closure: Publication.() -> Unit = {}) {
    (publication ?: Publication(this)).closure()
}