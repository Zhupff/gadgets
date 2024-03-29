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
                val widgetDsl = symbol.annotations.find {
                    it.annotationType.resolve().declaration.declareQualifiedName == WidgetDsl::class.java.canonicalName
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
                    WidgetDsl(alias, qualifiedName)
                } ?: return@forEach
                val qualifiedName = if (widgetDsl.qualifiedName.isNullOrEmpty()) {
                    when (symbol) {
                        is KSClassDeclaration -> symbol.declareQualifiedName
                        is KSPropertyDeclaration -> {
                            symbol.type.resolve().declaration.let { declaration ->
                                if (declaration is KSClassDeclaration && declaration.qualifiedName != null) {
                                    declaration.declareQualifiedName
                                } else {
                                    widgetDsl.qualifiedName
                                }
                            }
                        }
                        else -> return@forEach
                    }
                } else {
                    widgetDsl.qualifiedName
                }
                symbols.add(SymbolInfo(
                    symbol,
                    qualifiedName,
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
                                    receiver = ClassName("", info.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    parameters = listOf(
                                        ParameterSpec.unnamed(ClassName("", info.qualifiedName))
                                    ),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return ${info.qualifiedName}(context)\n")
                        .addCode(".initialize(id, size, null).also { it.block(it) }")
                        .returns(ClassName("", info.qualifiedName))
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
                                    receiver = ClassName("", info.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    parameters = listOf(
                                        ParameterSpec.unnamed(ClassName("", info.qualifiedName))
                                    ),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return ${info.qualifiedName}(context)\n")
                        .addCode(".initialize(id, layoutParams, null).also { it.block(it) }")
                        .returns(ClassName("", info.qualifiedName))
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
                                    receiver = ClassName("", info.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    parameters = listOf(
                                        ParameterSpec.unnamed(ClassName("", info.qualifiedName))
                                    ),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return ${info.qualifiedName}(context)\n")
                        .addCode(".initialize(id, size, this, index).also { it.block(it) }")
                        .returns(ClassName("", info.qualifiedName))
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
                                    receiver = ClassName("", info.qualifiedName)
                                        .copy(annotations = listOf(
                                            AnnotationSpec.builder(ClassName("zhupf.gadget.widget", "WidgetDslScope"))
                                                .build()
                                        )),
                                    parameters = listOf(
                                        ParameterSpec.unnamed(ClassName("", info.qualifiedName))
                                    ),
                                    returnType = UNIT,
                                )
                            ).build()
                        )
                        .addCode("return ${info.qualifiedName}(context)\n")
                        .addCode(".initialize(id, layoutParams, this, index).also { it.block(it) }")
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
        val widgetDsl: WidgetDsl,
    ) {
        override fun toString(): String = StringBuilder()
            .appendLine("symbol=$symbol")
            .appendLine("qualifiedName=$qualifiedName")
            .appendLine("widgetDsl {")
            .appendLine("  name=${widgetDsl.alias}")
            .appendLine("  qualifiedName=${widgetDsl.qualifiedName}")
            .appendLine("}")
            .toString()
    }
}