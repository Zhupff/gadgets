package gadget.theme

import java.io.File

internal open class ThemeMergeTask {

    companion object {

        fun getTaskName(variant: String): String {
            return ThemeMergeTask::class.java.simpleName + variant.replaceFirstChar { it.uppercaseChar() }
        }

        fun getAssetsDir(buildDir: File, variant: String): File {
            return buildDir.resolve("gadget_theme").resolve(variant).resolve("")
        }
    }
}