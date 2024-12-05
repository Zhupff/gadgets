package zhupff.gadgets

import groovy.lang.Closure
import groovy.lang.GroovyClassLoader
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap

open class Gadgets : Plugin<Project>, MutableMap<Any, Any> by ConcurrentHashMap() {

    val project: Project; get() = this[Project::class.java] as Project

    override fun apply(target: Project) {
        println("$target apply $this")
        clear()
        this[Project::class.java] = target
        target.extensions.add(javaClass, "gadgets", this)
        ServiceLoader.load(GadgetDelegate::class.java).forEach { gadgetDelegate ->
            gadgetDelegate.gadgets = this
            put(gadgetDelegate.name, gadgetDelegate)
        }
    }

    fun compose(closure: Closure<*>) {
        if (project.buildscript.sourceFile?.name?.endsWith(".kts") == true) {
            throw IllegalStateException("This method only for groovy script")
        }

        val code = StringBuilder()
            .appendLine("package zhupff.gadgets")
            .appendLine("class GadgetsCompose {")
            .appendLine("  final Gadgets gadgets")
            .appendLine("  GadgetsCompose(Gadgets gadgets) {")
            .appendLine("    this.gadgets = gadgets")
            .appendLine("  }")
        forEach { (name, gadgetDelegate) -> code
            .appendLine("  def ${name}(closure) {")
            .appendLine("    gadgets.get(\"${name}\").closure(closure)")
            .appendLine("  }")
        }
        code.appendLine("}")

        val gadgetsCompose = GroovyClassLoader()
            .parseClass(code.toString())
            .getConstructor(Gadgets::class.java)
            .newInstance(this)
        closure.delegate = gadgetsCompose
        closure.call()
    }

    fun publish() {
        project.let { p ->
            p.pluginManager.apply("maven-publish")
            p.afterEvaluate {
                p.extensions.configure(PublishingExtension::class.java) {
                    repositories {
                        mavenLocal()
                    }
                    publications {
                        create("MavenLocalPublication", MavenPublication::class.java) {
                            from(p.components.getByName(
                                if (p.pluginManager.hasPlugin("com.android.application") ||
                                    p.pluginManager.hasPlugin("com.android.library")) {
                                    "release"
                                } else {
                                    "java"
                                }
                            ))
                            groupId = p.group.toString()
                            artifactId = p.name
                            version = p.version.toString()
                        }
                    }
                }
            }
        }
    }
}