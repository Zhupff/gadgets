package gadget.theme

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import gadget.gradle.Gadget
import gadget.gradle.GadgetEx
import org.gradle.api.Project

@GadgetEx
class GadgetTheme : Gadget() {

    fun merge() {
        assert(this.project.isAndroidApplication)
        val appExtension = this.project.extensions.getByType(AppExtension::class.java)
        appExtension.applicationVariants.all variant@ {
            appExtension.sourceSets.getByName(this@variant.name).assets.srcDir(this@GadgetTheme.project.getBuildAssetsDir(this@variant.name))
            preBuildProvider.get().dependsOn(this@GadgetTheme.project.tasks.create(ThemeMergeTask.getTaskName(this@variant.name)))
        }
    }

    fun pack() {
        assert(this.project.isAndroidApplication)
        this.project.extensions.getByType(AppExtension::class.java).applicationVariants.all variant@ {
            outputs.all output@ {
                if (this@output.outputFile.name.endsWith(".apk")) {
                    findAllThemeMergeProjects(this@variant.name).forEach {
                        val themePackTask = this@GadgetTheme.project.tasks.register(ThemePackTask.getTaskName(this@variant.name), ThemePackTask::class.java) {
                            inputFilePath = this@output.outputFile.absolutePath
                            outputFile = it.getBuildAssetsDir(this@variant.name).resolve("theme").resolve(this@GadgetTheme.project.name)
                        }
                        it.tasks.named(ThemeMergeTask.getTaskName(this@variant.name)) {
                            themePackTask.dependsOn("package" + this@variant.name.replaceFirstChar { it.uppercaseChar() })
                            dependsOn(themePackTask)
                        }
                    }
                }
            }
        }
    }

    fun inject() {
        assert(this.project.isAndroidApplication)
        this.project.extensions.getByType(AppExtension::class.java).applicationVariants.all variant@ {
            findAllThemeMergeProjects(this@variant.name).forEach { p ->
                p.extensions.getByType(AppExtension::class.java).sourceSets.findByName(this@variant.name)?.let { ss ->
                    val src = this@GadgetTheme.project.projectDir.resolve("src").resolve("main")
                    ss.assets.srcDir(src.resolve("assets"))
                    ss.res.srcDir(src.resolve("res"))
                }
            }
        }
    }

    fun core(): String = dependency("gadget-theme-core")

    fun scheme(): String = dependency("gadget-theme-scheme")

    private fun findAllThemeMergeProjects(variant: String): List<Project> {
        return this.project.rootProject.allprojects.filter {
            it.isAndroidApplication
        }.filter {
            try {
                it.tasks.named(ThemeMergeTask.getTaskName(variant)) != null
            } catch (throwable: Throwable) {
                false
            }
        }
    }
}