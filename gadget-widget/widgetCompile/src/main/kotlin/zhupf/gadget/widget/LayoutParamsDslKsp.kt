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
                val layoutParamsDsl = symbol.annotations.find {
                    it.annotationType.resolve().declaration.declareQualifiedName == LayoutParamsDsl::class.java.canonicalName
                }?.arguments?.let { arguments ->
                    val alias = arguments.find {
                        it.name?.getShortName() == "alias"
                    }?.value as? String
                    val qualifiedName = arguments.find {
                        it.name?.getShortName() == "qualifiedName"
                    }?.value as? String ?: ""
                    if (alias.isNullOrEmpty()) {
                        return@forEach
                    }
                    LayoutParamsDsl(alias, qualifiedName)
                } ?: return@forEach
                val qualifiedName = if (layoutParamsDsl.qualifiedName.isNullOrEmpty()) {
                    when (symbol) {
                        is KSClassDeclaration -> symbol.declareQualifiedName
                        is KSPropertyDeclaration -> {
                            symbol.type.resolve().declaration.let { declaration ->
                                if (declaration is KSClassDeclaration && declaration.qualifiedName != null) {
                                    declaration.declareQualifiedName
                                } else {
                                    layoutParamsDsl.qualifiedName
                                }
                            }
                        }
                        else -> return@forEach
                    }
                } else {
                    layoutParamsDsl.qualifiedName
                }
                symbols.add(SymbolInfo(
                    symbol,
                    qualifiedName,
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
                    TypeAliasSpec.builder(info.layoutParamsDsl.alias, ClassName("", info.qualifiedName))
                        .build()
                )
                .addFunction(
                    FunSpec.builder(info.layoutParamsDsl.alias.toLowerCamelCase())
                        .receiver(ClassName("android.view", "View"))
                        .addParameter(
                            ParameterSpec.builder("block",
                                LambdaTypeName.get(
                                    receiver = ClassName("", info.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return layoutParamsAs<${info.qualifiedName}>(block)")
                        .returns(ClassName("", info.qualifiedName))
                        .addModifiers(KModifier.INLINE)
                        .build()
                )
                .build()
            file.writeTo(environment.codeGenerator, true, listOf(info.symbol.containingFile!!))
        }
    }

    private class SymbolInfo(
        val symbol: KSDeclaration,
        val qualifiedName: String,
        val layoutParamsDsl: LayoutParamsDsl,
    ) {
        override fun toString(): String = StringBuilder()
            .appendLine("symbol=$symbol")
            .appendLine("qualifiedName=$qualifiedName")
            .appendLine("layoutParamsDsl {")
            .appendLine("  name=${layoutParamsDsl.alias}")
            .appendLine("  qualifiedName=${layoutParamsDsl.qualifiedName}")
            .appendLine("}")
            .toString()
    }
}