package zhupff.gadgets.transform

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import java.io.File

class TransformContext(
    val project: Project
) {

    /** 是否是 Application 模块 */
    val isApplicationProject: Boolean = project.extensions.findByType(AppExtension::class.java) != null

    /** 是否是 Android-Library 模块 */
    val isLibraryProject: Boolean = project.extensions.findByType(LibraryExtension::class.java) != null

    val isDebug: Boolean = project.gradle.startParameter.taskNames.any { it.contains("debug", true) }

    val buildDir: File = project.buildDir
}