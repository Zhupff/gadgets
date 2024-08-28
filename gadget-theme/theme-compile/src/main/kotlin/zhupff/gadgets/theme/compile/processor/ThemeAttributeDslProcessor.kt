package zhupff.gadgets.theme.compile.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import zhupff.gadgets.theme.compile.common.cn_ThemeObject
import zhupff.gadgets.theme.compile.common.ps_AnyRes

object ThemeAttributeDslProcessor {

    data class Item(
        val alias: String,
        val packageName: String,
        val canonicalName: String,
    )

    fun buildFileSpec(item: Item): FileSpec {
        val cnItem: ClassName = ClassName("", item.canonicalName)
        return FileSpec.builder(item.packageName, "${item.alias.replaceFirstChar { it.uppercaseChar() }}_THEMEATTRIBUTEDSL")
            .addImport(cn_initialize, "")
            .addFunction(
                FunSpec.builder(item.alias)
                    .receiver(cn_ThemeObject)
                    .addParameter(ps_AnyRes)
                    .returns(cnItem)
                    .addCode("""
                        val attribute = ${item.canonicalName}().${cn_initialize.simpleName}(${ps_AnyRes.name})
                        addThemeAttribute(attribute)
                        return attribute
                    """.trimIndent())
                    .build()
            )
            .build()
    }

    private val cn_initialize: ClassName = ClassName("zhupff.gadgets.theme.dsl", "initialize")
}