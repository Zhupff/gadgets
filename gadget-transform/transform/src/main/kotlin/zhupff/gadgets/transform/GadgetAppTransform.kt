package zhupff.gadgets.transform

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

open class GadgetAppTransform(
    context: TransformContext,
    transformers: List<Transformer> = emptyList()
) : GadgetLibTransform(
    context,
    transformers,
) {

    override fun handleJarInput(
        input: JarInput,
        output: TransformOutputProvider,
        invocation: TransformInvocation
    ) {
        if (invocation.isIncremental) {
            when (input.status) {
                Status.ADDED,
                Status.CHANGED -> {
                    // do nothing
                }
                Status.REMOVED -> {
                    input.file.delete()
                    return
                }
                else -> { // Status.UNCHANGED or null
                    return
                }
            }
        }
        val tempFile = invocation.context.temporaryDir.resolve("temporary_${input.name}")
        if (tempFile.exists()) tempFile.delete()
        JarFile(input.file).use { jarFile ->
            FileOutputStream(tempFile).use { fos ->
                JarOutputStream(fos).use { jos ->
                    jarFile.entries().toList().forEach { jarEntry ->
                        jarFile.getInputStream(jarEntry).use { ins ->
                            var bytes = ins.readBytes()
                            if (jarEntry.name.endsWith(".class")) {
                                bytes = transform(invocation, bytes)
                            }
                            jos.putNextEntry(ZipEntry(jarEntry.name))
                            jos.write(bytes)
                            jos.closeEntry()
                        }
                    }
                }
            }
        }
        FileUtils.copyFile(
            tempFile,
            output.getContentLocation(input.name, input.contentTypes, input.scopes, Format.JAR)
        )
        if (tempFile.exists()) tempFile.delete()
    }
}