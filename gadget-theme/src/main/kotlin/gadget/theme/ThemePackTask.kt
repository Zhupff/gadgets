package gadget.theme

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@CacheableTask
internal open class ThemePackTask : DefaultTask() {

    companion object {
        fun getTaskName(variant: String): String {
            return ThemePackTask::class.java.simpleName + variant.replaceFirstChar { it.uppercaseChar() }
        }
    }

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.NONE)
    val src = project.projectDir.resolve("src")

    @get:Input
    lateinit var inputFilePath: String

    @OutputFile
    lateinit var outputFile: File

    @TaskAction
    fun action() {
        val inputFile = File(inputFilePath)
        if (!inputFile.exists()) return
        Files.copy(inputFile.toPath(), outputFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING)
    }
}