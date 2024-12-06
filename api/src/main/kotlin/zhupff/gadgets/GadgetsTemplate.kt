package zhupff.gadgets

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.text.SimpleDateFormat

abstract class GadgetsAndroidTemplate<E> : Gadgets() {

    open fun configure(namespace: String) {
        with(project) {
            androidExtension.apply {
                this.namespace = namespace
                compileSdk = 33
                defaultConfig {
                    minSdk = 24
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                kotlinOptions {
                    jvmTarget = JavaVersion.VERSION_17.toString()
                    freeCompilerArgs = freeCompilerArgs + listOf(
                        "-module-name",
                        project.path.replaceFirst(":", "").replace(":", "-")
                    )
                }
                sourceSets {
                    getByName("main") {
                        java.srcDir("src/main/kotlin")
                    }
                    getByName("debug") {
                        java.srcDir("src/debug/kotlin")
                    }
                    getByName("release") {
                        java.srcDir("src/release/kotlin")
                    }
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
                viewBinding {
                    enable = true
                }
            }
        }
    }

    open fun configure(namespace: String, closure: E.() -> Unit) {
        configure(namespace)
    }



    protected val Project.isApplication: Boolean
        get() = pluginManager.hasPlugin("com.android.application")

    protected val Project.isAndroidLibrary: Boolean
        get() = pluginManager.hasPlugin("com.android.library")

    protected val Project.applicationExtension: ApplicationExtension
        get() = extensions.getByType(ApplicationExtension::class.java)

    protected val Project.libraryExtension: LibraryExtension
        get() = extensions.getByType(LibraryExtension::class.java)

    protected val Project.androidExtension: CommonExtension<*, *, *, *, *>
        get() = if (isApplication)
            applicationExtension
        else if (isAndroidLibrary)
            libraryExtension
        else
            throw RuntimeException()

    protected fun CommonExtension<*, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
        (this as ExtensionAware).extensions.configure("kotlinOptions", block)
    }
}

open class GadgetsAndroid : GadgetsAndroidTemplate<LibraryExtension>() {

    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("com.android.library")
        target.pluginManager.apply("org.jetbrains.kotlin.android")
    }

    override fun configure(namespace: String, closure: LibraryExtension.() -> Unit) {
        super.configure(namespace, closure)
        project.libraryExtension.closure()
    }
}



open class GadgetsApplication : GadgetsAndroidTemplate<ApplicationExtension>() {

    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("com.android.application")
        target.pluginManager.apply("org.jetbrains.kotlin.android")
    }

    override fun configure(namespace: String) {
        super.configure(namespace)
        with(project) {
            applicationExtension.apply {
                defaultConfig {
                    applicationId = namespace
                    versionName = SimpleDateFormat("YY.MM.dd").format(System.currentTimeMillis())
                    versionCode = (System.currentTimeMillis() % (24 * 60 * 60 * 1000)).toInt()
                    println("applicationId=$applicationId, versionName=$versionName, versionCode=$versionCode")
                }
            }
        }
    }

    override fun configure(namespace: String, closure: ApplicationExtension.() -> Unit) {
        super.configure(namespace, closure)
        project.applicationExtension.closure()
    }
}



open class GadgetsJVM : Gadgets() {

    fun configure() {
        project.extensions.getByType(JavaPluginExtension::class.java).apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17

            sourceSets.getByName("main") {
                java.srcDir("src/main/kotlin")
            }
            sourceSets.getByName("debug") {
                java.srcDir("src/debug/kotlin")
            }
            sourceSets.getByName("release") {
                java.srcDir("src/release/kotlin")
            }
        }
    }
}