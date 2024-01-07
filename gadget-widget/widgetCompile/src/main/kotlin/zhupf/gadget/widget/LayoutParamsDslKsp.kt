package zhupf.gadget.widget

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.ksp.writeTo
import java.lang.StringBuilder

class LayoutParamsDslKsp : SymbolProcessorProvider, SymbolProcessor {

    private lateinit var environment: SymbolProcessorEnvironment

    private val symbols = ArrayList<SymbolInfo>()

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = apply {
        this.environment = environment
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(LayoutParamsDsl::class.java.canonicalName).forEach { symbol ->
            if (symbol is KSDeclaration) {
                val layoutParamsDsl = symbol.annotations.find {
                    it.annotationType.resolve().declaration.declareQualifiedName == LayoutParamsDsl::class.java.canonicalName
                }?.arguments?.let { arguments ->
                    val alias = arguments.find {
                        it.name?.getShortName() == "alias"
                    }?.value as? String
                    val qualifiedName = arguments.find {
                        it.name?.getShortName() == "qualifiedName"
                    }?.value as? String
                    if (alias.isNullOrEmpty() || qualifiedName.isNullOrEmpty()) {
                        return@forEach
                    }
                    LayoutParamsDsl(alias, qualifiedName)
                } ?: return@forEach
                symbols.add(SymbolInfo(
                    symbol,
                    layoutParamsDsl,
                ))
            }
        }
        return emptyList()
    }

    override fun finish() {
        symbols.forEach { info ->
            val file = FileSpec.builder(info.symbol.declarePackageName, "${info.layoutParamsDsl.alias}_DSL")
//                .addFileComment(info.toString())
                .addImport("zhupf.gadget.widget.dsl", "layoutParamsAs")
                .addTypeAlias(
                    TypeAliasSpec.builder(info.layoutParamsDsl.alias, ClassName("", info.layoutParamsDsl.qualifiedName))
                        .build()
                )
                .addFunction(
                    FunSpec.builder(info.layoutParamsDsl.alias.toLowerCamelCase())
                        .receiver(ClassName("android.view", "View"))
                        .addParameter(
                            ParameterSpec.builder("block",
                                LambdaTypeName.get(
                                    receiver = ClassName("", info.layoutParamsDsl.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return layoutParamsAs<${info.layoutParamsDsl.qualifiedName}>(block)")
                        .returns(ClassName("", info.layoutParamsDsl.qualifiedName))
                        .addModifiers(KModifier.INLINE)
                        .build()
                )
                .build()
            file.writeTo(environment.codeGenerator, true, listOf(info.symbol.containingFile!!))
        }
    }

    private class SymbolInfo(
        val symbol: KSDeclaration,
        val layoutParamsDsl: LayoutParamsDsl,
    ) {
        override fun toString(): String = StringBuilder()
            .appendLine("symbol=$symbol")
            .appendLine("layoutParamsDsl {")
            .appendLine("  name=${layoutParamsDsl.alias}")
            .appendLine("  qualifiedName=${layoutParamsDsl.qualifiedName}")
            .appendLine("}")
            .toString()
    }
}