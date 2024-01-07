package zhupf.gadget.widget

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
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
                val type = when (symbol) {
                    is KSClassDeclaration -> {
                        SymbolInfo.Type(
                            symbol.declarePackageName,
                            symbol.declareClassName,
                            symbol.declareQualifiedName,
                        )
                    }
                    is KSPropertyDeclaration -> {
                        symbol.type.resolve().declaration.let {
                            SymbolInfo.Type(
                                it.declarePackageName,
                                it.declareClassName,
                                it.declareQualifiedName,
                            )
                        }
                    }
                    else -> return@forEach
                }
                val layoutParamsDsl = symbol.annotations.find {
                    it.annotationType.resolve().declaration.declareQualifiedName == LayoutParamsDsl::class.java.canonicalName
                }?.arguments?.let { arguments ->
                    val alias = arguments.find {
                        it.name?.getShortName() == "alias"
                    }?.value as? String
                    if (alias.isNullOrEmpty()) {
                        return@forEach
                    }
                    LayoutParamsDsl(alias)
                } ?: return@forEach
                symbols.add(SymbolInfo(
                    symbol,
                    type,
                    layoutParamsDsl,
                ))
            }
        }
        return emptyList()
    }

    override fun finish() {
        symbols.forEach { info ->
            val file = FileSpec.builder(info.symbol.declarePackageName, info.layoutParamsDsl.alias)
                .addFileComment(info.toString())
                .addImport("zhupf.gadget.widget.dsl", "layoutParamsAs")
                .addTypeAlias(
                    TypeAliasSpec.builder(info.layoutParamsDsl.alias, ClassName("", info.type.qualifiedName))
                        .build()
                )
                .addFunction(
                    FunSpec.builder(info.layoutParamsDsl.alias.toLowerCamelCase())
                        .receiver(ClassName("android.view", "View"))
                        .addParameter(
                            ParameterSpec.builder("block",
                                LambdaTypeName.get(
                                    receiver = ClassName("", info.type.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    returnType = UNIT)
                            ).build()
                        )
                        .addCode("return layoutParamsAs<${info.type.qualifiedName}>(block)")
                        .returns(ClassName("", info.type.qualifiedName))
                        .addModifiers(KModifier.INLINE)
                        .build()
                )
                .build()
            file.writeTo(environment.codeGenerator, true, listOf(info.symbol.containingFile!!))
        }
    }

    private class SymbolInfo(
        val symbol: KSDeclaration,
        val type: Type,
        val layoutParamsDsl: LayoutParamsDsl,
    ) {
        class Type(
            val packageName: String,
            val className: String,
            val qualifiedName: String,
        ) {
            override fun toString(): String = StringBuilder()
                .appendLine("packageName=$packageName")
                .appendLine("className=$className")
                .appendLine("qualifiedName=$qualifiedName")
                .toString()
        }
        override fun toString(): String = StringBuilder()
            .appendLine("symbol=$symbol")
            .appendLine("type {")
            .appendLine(type.toString())
            .appendLine("}")
            .appendLine("layoutParamsDsl {")
            .appendLine("  name=${layoutParamsDsl.alias}")
            .appendLine("}")
            .toString()
    }
}