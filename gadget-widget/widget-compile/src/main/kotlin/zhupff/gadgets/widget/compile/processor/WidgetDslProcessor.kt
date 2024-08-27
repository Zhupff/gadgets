package zhupff.gadgets.widget.compile.processor

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.UNIT
import zhupff.gadgets.widget.compile.common.cn_DslScope
import zhupff.gadgets.widget.compile.common.cn_View
import zhupff.gadgets.widget.compile.common.cn_ViewGroup
import zhupff.gadgets.widget.compile.common.ps_Context
import zhupff.gadgets.widget.compile.common.ps_IdRes
import zhupff.gadgets.widget.compile.common.ps_IdString
import zhupff.gadgets.widget.compile.common.ps_Index
import zhupff.gadgets.widget.compile.common.ps_LayoutParams
import zhupff.gadgets.widget.compile.common.ps_Size

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
                    .addParameter(ps_IdRes)
                    .addParameter(ps_Size)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdRes.name}, ${ps_Size.name}, null).also { it.block(it) }")
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
                    .addParameter(ps_Context)
                    .addParameter(ps_IdString)
                    .addParameter(ps_Size)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdString.name}, ${ps_Size.name}, null).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .addParameter(ps_Context)
                    .addParameter(ps_IdString)
                    .addParameter(ps_LayoutParams)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdString.name}, ${ps_LayoutParams.name}, null).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .receiver(cn_ViewGroup)
                    .addParameter(ps_IdRes)
                    .addParameter(ps_Size)
                    .addParameter(ps_Index)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdRes.name}, ${ps_Size.name}, this, ${ps_Index.name}).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .receiver(cn_ViewGroup)
                    .addParameter(ps_IdRes)
                    .addParameter(ps_LayoutParams)
                    .addParameter(ps_Index)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdRes.name}, ${ps_LayoutParams.name}, this, ${ps_Index.name}).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .receiver(cn_ViewGroup)
                    .addParameter(ps_IdString)
                    .addParameter(ps_Size)
                    .addParameter(ps_Index)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdString.name}, ${ps_Size.name}, this, ${ps_Index.name}).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias)
                    .receiver(cn_ViewGroup)
                    .addParameter(ps_IdString)
                    .addParameter(ps_LayoutParams)
                    .addParameter(ps_Index)
                    .addParameter(ps_block(cnItem))
                    .addCode("return ${item.canonicalName}(context)\n")
                    .addCode("    .initialize(${ps_IdString.name}, ${ps_LayoutParams.name}, this, ${ps_Index.name}).also { it.block(it) }")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .build()
    }

    private val cn_initialize: ClassName = ClassName("zhupff.gadgets.widget.dsl", "initialize")

    private fun ps_block(cnItem: ClassName): ParameterSpec = ParameterSpec.builder("block",
        LambdaTypeName.get(
            receiver = cnItem
                .copy(annotations = listOf(
                    AnnotationSpec.builder(cn_DslScope).build()
                )),
            parameters = listOf(
                ParameterSpec.unnamed(cnItem)
            ),
            returnType = UNIT
        )
    ).build()
}