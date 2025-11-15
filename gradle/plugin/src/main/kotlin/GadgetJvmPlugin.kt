import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

open class GadgetJvmPlugin : GadgetPlugin() {

    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("org.jetbrains.kotlin.jvm")
        target.pluginManager.apply("java-library")
        target.pluginManager.apply("groovy")
        target.extensions.getByType(JavaPluginExtension::class.java).apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            sourceSets.getByName("main") {
                java.srcDir("src/main/kotlin")
            }
        }
        target.tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }
}