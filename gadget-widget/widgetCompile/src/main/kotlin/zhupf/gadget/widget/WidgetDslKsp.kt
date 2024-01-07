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
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.writeTo
import java.lang.StringBuilder

class WidgetDslKsp : SymbolProcessorProvider, SymbolProcessor {

    private lateinit var environment: SymbolProcessorEnvironment

    private val symbols = ArrayList<SymbolInfo>()

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = apply {
        this.environment = environment
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(WidgetDsl::class.java.canonicalName).forEach { symbol ->
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
                val widgetDsl = symbol.annotations.find {
                    it.annotationType.resolve().declaration.declareQualifiedName == WidgetDsl::class.java.canonicalName
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
                    WidgetDsl(alias, qualifiedName)
                } ?: return@forEach
                symbols.add(SymbolInfo(
                    symbol,
                    type,
                    widgetDsl,
                ))
            }
        }
        return emptyList()
    }

    override fun finish() {
        symbols.forEach { info ->
            val file = FileSpec.builder(info.symbol.declarePackageName, "${info.widgetDsl.alias}_DSL")
//                .addFileComment(info.toString())
                .addImport("zhupf.gadget.widget.dsl", "NO_ID")
                .addImport("zhupf.gadget.widget.dsl", "initialize")
                .addFunction(
                    FunSpec.builder(info.widgetDsl.alias)
                        .addParameter(
                            ParameterSpec("context", ClassName("android.content", "Context"))
                        )
                        .addParameter(
                            ParameterSpec.builder("id", INT)
                                .addAnnotation(ClassName("androidx.annotation", "IdRes"))
                                .defaultValue("NO_ID")
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("size", Pair::class.asClassName().parameterizedBy(INT, INT))
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("block",
                                LambdaTypeName.get(
                                    receiver = ClassName("", info.widgetDsl.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    parameters = listOf(
                                        ParameterSpec.unnamed(ClassName("", info.widgetDsl.qualifiedName))
                                    ),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return ${info.widgetDsl.qualifiedName}(context).initialize(id, size, null).also { it.block(it) }")
                        .returns(ClassName("", info.widgetDsl.qualifiedName))
                        .addModifiers(KModifier.INLINE)
                        .build()
                )
                .addFunction(
                    FunSpec.builder(info.widgetDsl.alias)
                        .addParameter(
                            ParameterSpec("context", ClassName("android.content", "Context"))
                        )
                        .addParameter(
                            ParameterSpec.builder("id", INT)
                                .addAnnotation(ClassName("androidx.annotation", "IdRes"))
                                .defaultValue("NO_ID")
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("layoutParams", ClassName("", "android.view.ViewGroup.LayoutParams"))
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("block",
                                LambdaTypeName.get(
                                    receiver = ClassName("", info.widgetDsl.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    parameters = listOf(
                                        ParameterSpec.unnamed(ClassName("", info.widgetDsl.qualifiedName))
                                    ),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return ${info.widgetDsl.qualifiedName}(context).initialize(id, layoutParams, null).also { it.block(it) }")
                        .returns(ClassName("", info.widgetDsl.qualifiedName))
                        .addModifiers(KModifier.INLINE)
                        .build()
                )
                .addFunction(
                    FunSpec.builder(info.widgetDsl.alias)
                        .receiver(ClassName("android.view", "ViewGroup"))
                        .addParameter(
                            ParameterSpec.builder("id", INT)
                                .addAnnotation(ClassName("androidx.annotation", "IdRes"))
                                .defaultValue("NO_ID")
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("size", Pair::class.asClassName().parameterizedBy(INT, INT))
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("index", INT)
                                .defaultValue("-1")
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("block",
                                LambdaTypeName.get(
                                    receiver = ClassName("", info.widgetDsl.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    parameters = listOf(
                                        ParameterSpec.unnamed(ClassName("", info.widgetDsl.qualifiedName))
                                    ),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return ${info.widgetDsl.qualifiedName}(context).initialize(id, size, this, index).also { it.block(it) }")
                        .returns(ClassName("", info.widgetDsl.qualifiedName))
                        .addModifiers(KModifier.INLINE)
                        .build()
                )
                .addFunction(
                    FunSpec.builder(info.widgetDsl.alias)
                        .receiver(ClassName("android.view", "ViewGroup"))
                        .addParameter(
                            ParameterSpec.builder("id", INT)
                                .addAnnotation(ClassName("androidx.annotation", "IdRes"))
                                .defaultValue("NO_ID")
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("layoutParams", ClassName("", "android.view.ViewGroup.LayoutParams"))
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("index", INT)
                                .defaultValue("-1")
                                .build()
                        )
                        .addParameter(
                            ParameterSpec.builder("block",
                                LambdaTypeName.get(
                                    receiver = ClassName("", info.widgetDsl.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    parameters = listOf(
                                        ParameterSpec.unnamed(ClassName("", info.widgetDsl.qualifiedName))
                                    ),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return ${info.widgetDsl.qualifiedName}(context).initialize(id, layoutParams, this, index).also { it.block(it) }")
                        .returns(ClassName("", info.widgetDsl.qualifiedName))
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
        val widgetDsl: WidgetDsl,
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
            .appendLine("dsl {")
            .appendLine("}")
            .appendLine("widgetDsl {")
            .appendLine("  name=${widgetDsl.alias}")
            .appendLine("  qualifiedName=${widgetDsl.qualifiedName}")
            .appendLine("}")
            .toString()
    }
}