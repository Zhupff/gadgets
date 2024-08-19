package zhupff.gadgets.widget.compile.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import zhupff.gadgets.widget.compile.common.cn_View
import zhupff.gadgets.widget.compile.common.cn_ViewGroup
import zhupff.gadgets.widget.compile.common.cn_initialize
import zhupff.gadgets.widget.compile.common.ps_Context
import zhupff.gadgets.widget.compile.common.ps_IdRes
import zhupff.gadgets.widget.compile.common.ps_LayoutParams
import zhupff.gadgets.widget.compile.common.ps_block
import zhupff.gadgets.widget.compile.common.ps_index
import zhupff.gadgets.widget.compile.common.ps_size

object WidgetDslProcessor {

    data class Item(
        val alias: String,
        val packageName: String,
        val canonicalName: String,
    )

    fun buildFileSpec(item: Item): FileSpec {
        val cnItem: ClassName = ClassName("", item.canonicalName)
        return FileSpec.builder(item.packageName, "${item.alias}_WIDGETDSL")
            .addImport(cn_View, "")
            .addImport(cn_initialize, "")
            .addFunction(
                FunSpec.builder(item.alias)
                    .addParameter(ps_Context)
                    .addParameter(ps_size)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(View.NO_ID, ${ps_size.name}, null).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .addParameter(ps_Context)
                    .addParameter(ps_LayoutParams)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(View.NO_ID, ${ps_LayoutParams.name}, null).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .addParameter(ps_Context)
                    .addParameter(ps_IdRes)
                    .addParameter(ps_size)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdRes.name}, ${ps_size.name}, null).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .addParameter(ps_Context)
                    .addParameter(ps_IdRes)
                    .addParameter(ps_LayoutParams)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdRes.name}, ${ps_LayoutParams.name}, null).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .receiver(cn_ViewGroup)
                    .addParameter(ps_size)
                    .addParameter(ps_index)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(View.NO_ID, ${ps_size.name}, this, ${ps_index.name}).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .receiver(cn_ViewGroup)
                    .addParameter(ps_LayoutParams)
                    .addParameter(ps_index)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(View.NO_ID, ${ps_LayoutParams.name}, this, ${ps_index.name}).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .receiver(cn_ViewGroup)
                    .addParameter(ps_IdRes)
                    .addParameter(ps_size)
                    .addParameter(ps_index)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdRes.name}, ${ps_size.name}, this, ${ps_index.name}).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .receiver(cn_ViewGroup)
                    .addParameter(ps_IdRes)
                    .addParameter(ps_LayoutParams)
                    .addParameter(ps_index)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdRes.name}, ${ps_LayoutParams.name}, this, ${ps_index.name}).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .build()
    }
}