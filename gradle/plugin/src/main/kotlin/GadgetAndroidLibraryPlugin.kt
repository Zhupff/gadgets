import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project

open class GadgetAndroidLibraryPlugin : GadgetAndroidPlugin<LibraryExtension>() {

    override val androidExtension: LibraryExtension
        get() = this.project.extensions.getByType(LibraryExtension::class.java)

    override fun apply(target: Project) {
        super.apply(target)
        target.pluginManager.apply("com.android.library")
        target.pluginManager.apply("org.jetbrains.kotlin.android")
    }
}