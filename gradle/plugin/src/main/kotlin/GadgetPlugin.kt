import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

abstract class GadgetPlugin : Plugin<Project> {

    protected lateinit var project: Project
        private set

    protected val libs: VersionCatalog by lazy {
        this.project.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
    }

    override fun apply(target: Project) {
        this.project = target
        if (System.getenv("JITPACK").toBoolean()) {
            this.project.group = System.getenv("GROUP") + '.' + System.getenv("ARTIFACT")
            this.project.version = System.getenv("VERSION")
        } else {
            this.project.group = "gadgets"
            this.project.version = "0"
        }
        target.extensions.add(this.javaClass, "gadget", this)
    }

    fun publish() {
        this.project.pluginManager.apply("maven-publish")
        this.project.afterEvaluate {
            this.project.extensions.configure(PublishingExtension::class.java) {
                repositories {
                    mavenLocal()
                }
                publications {
                    create("MavenLocalPublication", MavenPublication::class.java) {
                        from(this@GadgetPlugin.project.components.getByName(
                            if (this@GadgetPlugin.project.pluginManager.hasPlugin("com.android.application") ||
                                this@GadgetPlugin.project.pluginManager.hasPlugin("com.android.library")) {
                                "release"
                            } else {
                                "java"
                            }
                        ))
                        groupId    = this@GadgetPlugin.project.group.toString()
                        artifactId = this@GadgetPlugin.project.name
                        version    = this@GadgetPlugin.project.version.toString()
                    }
                }
            }
        }
    }



    protected fun project(name: String): Project = project.rootProject.findProject(name)!!

    protected fun DependencyHandler.api(notation: Any) {
        add("api", notation)
    }

    protected fun DependencyHandler.compileOnly(notation: Any) {
        add("compileOnly", notation)
    }

    protected fun DependencyHandler.implementation(notation: Any) {
        add("implementation", notation)
    }

    protected fun DependencyHandler.androidTestImplementation(notation: Any) {
        add("androidTestImplementation", notation)
    }

    protected fun DependencyHandler.kapt(notation: Any) {
        add("kapt", notation)
    }
}