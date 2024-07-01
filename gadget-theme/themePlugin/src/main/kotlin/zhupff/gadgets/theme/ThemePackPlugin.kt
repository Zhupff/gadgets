package zhupff.gadgets.theme

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ThemePackPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.getByType(AppExtension::class.java).applicationVariants.all {
            outputs.all {
                if (outputFile.name.endsWith(".apk")) {
                    val apkFile = outputFile
                    target.rootProject.allprojects.find {
                        it.plugins.hasPlugin("zhupff.gadgets.theme.merge") || it.plugins.hasPlugin(ThemeMergePlugin::class.java)
                    }?.let { p ->
                        val task = target.tasks.register("ThemePack${variantName.toCamelCase()}", ThemePackTask::class.java) {
                            inputFilePath = apkFile.path
                            outputFile = p.buildDir.resolve("themepacks${File.separator}${variantName}${File.separator}themepacks${File.separator}${target.name}")
                        }
                        p.tasks.named("ThemeMerge${variantName}") {
                            task.dependsOn("package${variantName.toCamelCase()}")
                            dependsOn(task)
                        }
                    }
                }
            }
        }
    }
}