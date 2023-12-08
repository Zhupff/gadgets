import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.crash.afterEvaluate
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

abstract class Configuration internal constructor(
    val gadget: Gadget,
) {

    internal open fun configure() {
        if (gadget[Configuration::class.java] != null) throw IllegalStateException("Configuration already set")
        gadget[Configuration::class.java] = this
        println("$gadget configure")
    }

    fun publish() {
        gadget.project.pluginManager.apply("maven-publish")
        afterEvaluate {
            gadget.project.extensions.configure(PublishingExtension::class.java) {
                repositories {
                    mavenLocal()
                }
                publications {
                    create("MavenLocalPublication", MavenPublication::class.java) {
                        from(
                            gadget.project.components.getByName(
                                if (this@Configuration is AndroidConfiguration)
                                    "release"
                                else if (this@Configuration is JvmConfiguration)
                                    "java"
                                else
                                    throw IllegalStateException("$gadget can't publish")
                            )
                        )
                        artifactId = gadget.project.name
//                        version = "0"
                    }
                }
            }
        }
    }


    class AndroidConfiguration internal constructor(
        gadget: Gadget,
        val namespace: String,
    ) : Configuration(
        gadget,
    ) {

        override fun configure() {
            super.configure()
            val androidExtension = when (gadget) {
                is GadgetApplication -> gadget.project.extensions.getByType(ApplicationExtension::class.java)
                is GadgetAndroid -> gadget.project.extensions.getByType(LibraryExtension::class.java)
                else -> throw IllegalArgumentException("Invalid gadget: $gadget")
            }
            androidExtension.apply {
                namespace = this@AndroidConfiguration.namespace
                compileSdk = 33
                defaultConfig {
                    minSdk = 24
                    if (this is ApplicationDefaultConfig) {
                        applicationId = this@AndroidConfiguration.namespace
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                (this as? ExtensionAware)?.extensions?.configure<KotlinJvmOptions>("kotlinOptions") {
                    jvmTarget = JavaVersion.VERSION_17.toString()
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }
        }
    }


    class JvmConfiguration internal constructor(
        gadget: Gadget,
    ) : Configuration(
        gadget,
    ) {
        override fun configure() {
            super.configure()
            gadget.project.extensions.getByType(JavaPluginExtension::class.java).apply {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
}


fun GadgetApplication.configuration(
    namespace: String,
    closure: Configuration.AndroidConfiguration.() -> Unit = {},
) {
    Configuration.AndroidConfiguration(this, namespace).apply {
        configure()
        closure(this)
    }
}

fun GadgetAndroid.configuration(
    namespace: String,
    closure: Configuration.() -> Unit = {},
) {
    Configuration.AndroidConfiguration(this, namespace).apply {
        configure()
        closure(this)
    }
}

fun GadgetJvm.configuration(
    closure: Configuration.JvmConfiguration.() -> Unit = {},
) {
    Configuration.JvmConfiguration(this).apply {
        configure()
        closure(this)
    }
}