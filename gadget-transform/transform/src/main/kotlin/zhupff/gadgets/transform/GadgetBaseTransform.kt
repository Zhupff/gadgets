package zhupff.gadgets.transform

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent.ContentType
import com.android.build.api.transform.QualifiedContent.DefaultContentType
import com.android.build.api.transform.QualifiedContent.Scope
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import zhupff.gadgets.basic.printlnF

open class GadgetBaseTransform @JvmOverloads constructor(
    protected val context: TransformContext,
    protected val transformers: List<Transformer> = emptyList(),
) : Transform() {

    override fun getName(): String = "${this::class.java.simpleName}(${hashCode()})"

    override fun getInputTypes(): MutableSet<ContentType> = mutableSetOf(DefaultContentType.CLASSES)

    override fun getScopes(): MutableSet<Scope> = if (context.isApplicationProject) {
        mutableSetOf(Scope.PROJECT, Scope.SUB_PROJECTS, Scope.EXTERNAL_LIBRARIES)
    } else if (context.isLibraryProject) {
        mutableSetOf(Scope.PROJECT)
    } else {
        throw IllegalStateException("${context.project} is neither app-module nor lib-module!")
    }

    override fun isIncremental(): Boolean = true

    override fun transform(transformInvocation: TransformInvocation) {
        val outputProvider = transformInvocation.outputProvider.also {
            if (!transformInvocation.isIncremental) {
                it.deleteAll()
            }
        }

        // before transform begin
        val beforeTransformTimestamp = System.currentTimeMillis()
        printlnF {
            append("${v(beforeTransformTimestamp)}: ${name} begin transform, incremental=${d(transformInvocation.isIncremental)}")
        }
        transformers.forEach { it.beforeTransform(context, transformInvocation) }

        transformInvocation.inputs.forEach { invocationInputs ->
            invocationInputs.jarInputs.forEach { input ->
                handleJarInput(input, outputProvider, transformInvocation)
            }
            invocationInputs.directoryInputs.forEach { input ->
                handleDirInput(input, outputProvider, transformInvocation)
            }
        }

        // after transform end
        transformers.forEach { it.afterTransform(context, transformInvocation) }
        val afterTransformTimestamp = System.currentTimeMillis()
        printlnF {
            append("${v(afterTransformTimestamp)}: ${name} end transform, duration=${d(afterTransformTimestamp - beforeTransformTimestamp)}")
        }
    }

    protected open fun handleJarInput(
        input: JarInput,
        output: TransformOutputProvider,
        invocation: TransformInvocation,
    ) {
        FileUtils.copyFile(
            input.file,
            output.getContentLocation(input.name, input.contentTypes, input.scopes, Format.JAR)
        )
    }

    protected open fun handleDirInput(
        input: DirectoryInput,
        output: TransformOutputProvider,
        invocation: TransformInvocation,
    ) {
        FileUtils.copyDirectory(
            input.file,
            output.getContentLocation(input.name, input.contentTypes, input.scopes, Format.DIRECTORY)
        )
    }

    protected open fun transform(
        invocation: TransformInvocation,
        bytes: ByteArray,
    ): ByteArray {
        val classNode = ClassNodeWrapper(bytes)
        transformers.forEach {
            it.transform(classNode)
        }
        if (classNode.isDirty()) {
            return classNode.toByteArray()
        }
        return bytes
    }




    interface Transformer {

        fun beforeTransform(context: TransformContext, invocation: TransformInvocation) {}

        fun afterTransform(context: TransformContext, invocation: TransformInvocation) {}

        fun transform(classNode: ClassNodeWrapper)
    }
}