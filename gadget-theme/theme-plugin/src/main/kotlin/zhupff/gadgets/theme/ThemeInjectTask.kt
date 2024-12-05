package zhupff.gadgets.theme

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
open class ThemeInjectTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.NONE)
    val src = project.projectDir.resolve("src/main")

    @OutputDirectory
    lateinit var outputDir: File

    @TaskAction
    fun action() {
        val themeInjectExtension = project.extensions.getByType(ThemeInjectExtension::class.java)
        val prefix = themeInjectExtension.prefix
        val variant = themeInjectExtension.variant
        if (prefix.isNullOrEmpty() || variant.isNullOrEmpty()) {
            throw IllegalArgumentException("Theme inject, prefix should not be null or empty!")
        }


        if (outputDir.exists()) {
            outputDir.deleteRecursively()
        }
        outputDir.mkdirs()
        if (!src.exists() || !src.isDirectory) {
            return
        }

        val tempDir = project.buildDir.resolve("themeinjects").also {
            if (it.exists()) it.deleteRecursively()
            it.mkdirs()
        }
        val assetsDir = src.resolve("assets")
        if (assetsDir.exists()) {
            val tempAssetsDir = tempDir.resolve("assets").apply { mkdirs() }
            assetsDir.copyRecursively(tempAssetsDir, true)
            tempAssetsDir.walkTopDown().forEach { processFile(it, themeInjectExtension) }
        }
        val resDir = src.resolve("res")
        if (resDir.exists()) {
            val tempResDir = tempDir.resolve("res").apply { mkdirs() }
            resDir.copyRecursively(tempResDir, true)
            tempResDir.walkTopDown().forEach { processFile(it, themeInjectExtension) }
        }

        tempDir.copyRecursively(outputDir, true)
        tempDir.deleteRecursively()
    }

    private fun processFile(file: File, ext: ThemeInjectExtension) {
        if (!file.isFile) return
        val fileExtension = file.extension
        val variantPrefix = ext.variantPrefix
        if (fileExtension == "xml") {
            val reader = file.reader(Charsets.UTF_8)
            var content = reader.readText()
            reader.close()
            content = content.replace(ext.prefix, variantPrefix)
            val writer = file.writer(Charsets.UTF_8)
            writer.write(content)
            writer.close()
        } else if (!file.name.startsWith(ext.prefix)) {
            file.delete()
            return
        }
        if (file.name.startsWith(ext.prefix)) {
            val renameFile = File(file.path.replace(ext.prefix, variantPrefix))
            file.renameTo(renameFile)
        }
    }
}