import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project

open class GadgetAndroidApplicationPlugin : GadgetAndroidPlugin<ApplicationExtension>() {

    override val androidExtension: ApplicationExtension
        get() = this.project.extensions.getByType(ApplicationExtension::class.java)

    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("com.android.application")
        target.pluginManager.apply("org.jetbrains.kotlin.android")
    }

    open fun android(applicationID: String, namespace: String) {
        android(namespace)
        androidExtension.apply {
            defaultConfig.apply {
                applicationId = applicationID
                targetSdk = 35
            }
            buildTypes {
                debug {
                    isMinifyEnabled = false
                    isShrinkResources = false
                }
                release {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                }
            }
        }
    }

    open fun android(applicationID: String, namespace: String, closure: ApplicationExtension.() -> Unit) {
        android(applicationID, namespace)
        androidExtension.closure()
    }
}