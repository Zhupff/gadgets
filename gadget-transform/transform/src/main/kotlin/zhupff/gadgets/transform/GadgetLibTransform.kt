package zhupff.gadgets.transform

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import zhupff.gadgets.basic.printlnF
import java.io.File

open class GadgetLibTransform(
    context: TransformContext,
    transformers: List<Transformer> = emptyList(),
) : GadgetBaseTransform(
    context,
    transformers,
) {

    override fun handleDirInput(
        input: DirectoryInput,
        output: TransformOutputProvider,
        invocation: TransformInvocation
    ) {
        printlnF {
            append("${name} handleDirInput: ${v(input.name)}")
        }
        val desDir = output.getContentLocation(input.name, input.contentTypes, input.scopes, Format.DIRECTORY)
        if (invocation.isIncremental) {
            val srcDirPath = input.file.absolutePath
            val desDirPath = desDir.absolutePath
            input.changedFiles.forEach { (file, status) ->
                when (status) {
                    Status.ADDED,
                    Status.CHANGED -> {
                        val desFile = File(file.absolutePath.replace(srcDirPath, desDirPath))
                        if (file.isFile) {
                            if (!desFile.exists()) {
                                desFile.parentFile?.let { desParentFile ->
                                    if (!desParentFile.mkdirs() || !desParentFile.isDirectory) {
                                        throw IllegalStateException("Can not create directory: ${desParentFile.absolutePath}")
                                    }
                                } ?: throw IllegalStateException("${desFile} 's parent is null.")
                                desFile.createNewFile()
                            }
                            if (file.name.endsWith(".class")) {
                                val bytes = transform(invocation, file.readBytes())
                                file.writeBytes(bytes)
                            }
                            FileUtils.copyFile(file, desFile)
                        }
                    }
                    Status.REMOVED -> {
                        FileUtils.deleteIfExists(file)
                    }
                    else -> { // Status.UNCHANGED or null
                        // do nothing
                    }
                }
            }
        } else {
            input.file.walk()
                .filter {
                    it.isFile && it.name.endsWith(".class")
                }
                .forEach {
                    val bytes = transform(invocation, it.readBytes())
                    it.writeBytes(bytes)
                }
            FileUtils.copyDirectory(input.file, desDir)
        }
    }
}