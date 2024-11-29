package zhupff.gadgets.theme

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ThemeInjectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.add(ThemeInjectExtension::class.java, "themeInject", ThemeInjectExtension())
        target.extensions.getByType(AppExtension::class.java).applicationVariants.all {
            target.rootProject.allprojects.find {
                it.plugins.hasPlugin("zhupff.gadgets.theme.merge") || it.plugins.hasPlugin(ThemeMergePlugin::class.java)
            }?.let { p ->
                val outputDir = p.buildDir.resolve("themeinjects${File.separator}${variantName}${File.separator}${target.name}")
                p.extensions.getByType(AppExtension::class.java).let { appExtension ->
                    appExtension.applicationVariants.all {
                        appExtension.sourceSets.getByName(variantName).let { ss ->
                            ss.assets.srcDir(outputDir.resolve("assets"))
                            ss.res.srcDir(outputDir.resolve("res"))
                        }
                    }
                }
                val task = target.tasks.register("ThemeInject${variantName.toCamelCase()}", ThemeInjectTask::class.java) {
                    this.outputDir = outputDir
                }
                p.tasks.named("ThemeMerge${variantName.toCamelCase()}") {
                    dependsOn(task)
                }
            }
        }
    }
}