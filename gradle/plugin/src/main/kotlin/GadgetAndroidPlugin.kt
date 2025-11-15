import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

abstract class GadgetAndroidPlugin<E : CommonExtension<*, *, *, *, *, *>> : GadgetPlugin() {

    protected abstract val androidExtension: E

    open fun android(namespace: String) {
        androidExtension.namespace = namespace
        androidExtension.apply {
            compileSdk = 35
            defaultConfig.minSdk = 24
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
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
        }
        this.project.tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
                freeCompilerArgs.addAll(
                    "-module-name",
                    this@GadgetAndroidPlugin.project.path.replaceFirst(":", "").replace(":", "-"),
                )
            }
        }
    }

    fun android(namespace: String, closure: E.() -> Unit) {
        android(namespace)
        androidExtension.closure()
    }

    fun enableViewBinding() {
        androidExtension.viewBinding.enable = true
    }

    fun enableJunitTest() {
        androidExtension.defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        this.project.dependencies {
            androidTestImplementation(libs.findLibrary("junit-android").get())
            androidTestImplementation(libs.findLibrary("espresso-core").get())
        }
    }

    fun DependencyHandler.androidx() {
        implementation(libs.findLibrary("androidx-appcompat").get())
        implementation(libs.findLibrary("androidx-core-ktx").get())
    }

    fun DependencyHandler.startup() {
        implementation(libs.findLibrary("androidx-startup").get())
    }
}