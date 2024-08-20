package zhupff.gadgets.widget.compile.processor

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.UNIT
import zhupff.gadgets.widget.compile.common.cn_DslScope
import zhupff.gadgets.widget.compile.common.cn_View

object LayoutParamsDslProcessor {

    data class Item(
        val alias: String,
        val packageName: String,
        val canonicalName: String,
    )

    fun buildFileSpec(item: Item): FileSpec {
        val cnItem: ClassName = ClassName("", item.canonicalName)
        return FileSpec.builder(item.packageName, "${item.alias}_LAYOUTPARAMSDSL")
            .addImport(cn_layoutParamsAs, "")
            .addTypeAlias(TypeAliasSpec.builder(item.alias.replaceFirstChar { it.uppercaseChar() }, cnItem).build())
            .addFunction(
                FunSpec.builder(item.alias.replaceFirstChar { it.lowercaseChar() })
                    .receiver(cn_View)
                    .addParameter(ps_block(cnItem))
                    .addCode("return layoutParamsAs<${item.canonicalName}>(block)")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .addFunction(
                FunSpec.builder(item.alias.replaceFirstChar { it.lowercaseChar() })
                    .receiver(cn_View)
                    .addParameter(
                        ParameterSpec.builder("init",
                            LambdaTypeName.get(
                                returnType = cnItem,
                            )
                        ).build()
                    )
                    .addParameter(ps_block(cnItem))
                    .addCode("return layoutParamsAs<${item.canonicalName}>(init, block)")
                    .returns(cnItem)
                    .addModifiers(KModifier.INLINE)
                    .build()
            )
            .build()
    }

    private val cn_layoutParamsAs: ClassName = ClassName("zhupff.gadgets.widget.dsl", "layoutParamsAs")

    private fun ps_block(cnItem: ClassName): ParameterSpec = ParameterSpec.builder("block",
        LambdaTypeName.get(
            receiver = cnItem
                .copy(annotations = listOf(
                    AnnotationSpec.builder(cn_DslScope).build()
                )),
            returnType = UNIT
        )
    ).build()
}