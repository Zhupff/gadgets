package zhupff.gadgets.transform

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import zhupff.gadgets.GadgetDelegate
import zhupff.gadgets.GadgetName

@GadgetName("Transform")
class GadgetTransform : GadgetDelegate() {

    private val context: TransformContext = TransformContext(gadgets.project)

    private val transformers = ArrayList<GadgetBaseTransform.Transformer>()

    private val transforms = ArrayList<Transform>()

    fun register(transform: Transform) {
        transforms.add(transform)
    }

    fun register(transformer: GadgetBaseTransform.Transformer) {
        transformers.add(transformer)
    }

    override fun beforeClosure() {
        super.beforeClosure()
        transformers.clear()
        transforms.clear()
    }

    override fun afterClosure() {
        super.afterClosure()
        val extension: BaseExtension = when {
            context.isApplicationProject -> gadgets.project.extensions.getByType(AppExtension::class.java)
            context.isLibraryProject -> gadgets.project.extensions.getByType(LibraryExtension::class.java)
            else -> return
        }
        if (transformers.isNotEmpty()) {
            when (extension) {
                is AppExtension -> extension.registerTransform(GadgetAppTransform(context, transformers))
                is LibraryExtension -> extension.registerTransform(GadgetLibTransform(context, transformers))
            }
        }
        transforms.forEach { extension.registerTransform(it) }
    }
}