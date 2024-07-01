package zhupff.gadgets.theme

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ThemeMergePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.getByType(AppExtension::class.java).let { appExtension ->
            appExtension.applicationVariants.all {
                appExtension.sourceSets.getByName(variantName).assets.srcDir(target.buildDir.resolve("themepacks${File.separator}${variantName}"))
                preBuildProvider.get().dependsOn(target.tasks.create("ThemeMerge${variantName.toCamelCase()}"))
            }
        }
    }
}