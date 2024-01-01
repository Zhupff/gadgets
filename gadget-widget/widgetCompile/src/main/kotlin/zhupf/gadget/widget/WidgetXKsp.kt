package zhupf.gadget.widget

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import java.lang.StringBuilder

class WidgetXKsp : SymbolProcessorProvider, SymbolProcessor {

    private lateinit var environment: SymbolProcessorEnvironment

    private val symbols = ArrayList<SymbolInfo>()

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = apply {
        this.environment = environment
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(WidgetX::class.java.canonicalName).forEach { symbol ->
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
                val widgetX = symbol.annotations.find {
                    it.annotationType.resolve().declaration.declareQualifiedName == WidgetX::class.java.canonicalName
                }?.arguments?.let { arguments ->
                    val name = arguments.find {
                        it.name?.getShortName() == "name"
                    }?.value as? String ?: throw IllegalStateException("WidgetX.name not valid: $symbol")
                    val cornerClip = arguments.find {
                        it.name?.getShortName() == "cornerClip"
                    }?.value as? Boolean ?: false
                    val windowFit = arguments.find {
                        it.name?.getShortName() == "windowFit"
                    }?.value as? Boolean ?: false
                    WidgetX(name, cornerClip, windowFit)
                } ?: throw IllegalStateException("WidgetX not found: $symbol")
                symbols.add(SymbolInfo(
                    symbol,
                    type,
                    widgetX,
                ))
            } else return@forEach
        }
        return emptyList()
    }

    override fun finish() {
        symbols.forEach { info ->
            val file = FileSpec.builder(info.symbol.declarePackageName, info.widgetX.name)
//                .addFileComment(info.toString())
                .addType(
                    TypeSpec.classBuilder(info.widgetX.name)
                        .addModifiers(KModifier.OPEN)
                        .superclass(ClassName(info.type.packageName, info.type.className))
                        .addSuperclassConstructorParameter("context, attrs")
                        .primaryConstructor(
                            FunSpec.constructorBuilder()
                                .addAnnotation(JvmOverloads::class)
                                .addParameter(
                                    ParameterSpec.builder("context", ClassName("android.content", "Context"))
                                        .build()
                                )
                                .addParameter(
                                    ParameterSpec.builder("attrs", ClassName("android.util", "AttributeSet").copy(true))
                                        .defaultValue("null")
                                        .build()
                                )
                                .build()
                        )
                        .alsoIf(info.widgetX.cornerClip) { builder ->
                            builder.addSuperinterface(ClassName("zhupf.gadget.widget.attribute", "CornerClip"))
                                .addProperty(
                                    PropertySpec.builder("cornerClip", ClassName("zhupf.gadget.widget.attribute", "CornerClipDelegate"))
                                        .addModifiers(KModifier.OVERRIDE)
                                        .initializer("CornerClipDelegate(this, attrs)")
                                        .build()
                                )
                                .addFunction(
                                    FunSpec.builder("draw")
                                        .addModifiers(KModifier.OVERRIDE)
                                        .addParameter(
                                            ParameterSpec("canvas", ClassName("android.graphics", "Canvas"))
                                        )
                                        .addStatement("canvas.save()")
                                        .addStatement("cornerClip.clipCorner(canvas)")
                                        .addStatement("super.draw(canvas)")
                                        .addStatement("cornerClip.drawBorder(canvas)")
                                        .addStatement("canvas.restore()")
                                        .build()
                                )
                                .addFunction(
                                    FunSpec.builder("dispatchDraw")
                                        .addModifiers(KModifier.OVERRIDE)
                                        .addParameter(
                                            ParameterSpec("canvas", ClassName("android.graphics", "Canvas"))
                                        )
                                        .addStatement("canvas.save()")
                                        .addStatement("cornerClip.clipCorner(canvas)")
                                        .addStatement("super.dispatchDraw(canvas)")
                                        .addStatement("canvas.restore()")
                                        .build()
                                )
                        }
                        .alsoIf(info.widgetX.windowFit) { builder ->
                            builder.addSuperinterface(ClassName("zhupf.gadget.widget.attribute", "WindowFit"))
                                .addProperty(
                                    PropertySpec.builder("windowFit", ClassName("zhupf.gadget.widget.attribute", "WindowFitDelegate"))
                                        .addModifiers(KModifier.OVERRIDE)
                                        .initializer("WindowFitDelegate(this, attrs)")
                                        .build()
                                )
                                .addFunction(
                                    FunSpec.builder("onApplyWindowInsets")
                                        .addModifiers(KModifier.OVERRIDE)
                                        .addParameter(
                                            ParameterSpec.builder("insets", ClassName("android.view", "WindowInsets").copy(true))
                                                .build()
                                        )
                                        .returns(ClassName("android.view", "WindowInsets").copy(true))
                                        .addStatement("windowFit.onWindowInsetsApplied(insets)")
                                        .addStatement("return super.onApplyWindowInsets(insets)")
                                        .build()
                                )
                        }
                        .build()
                )
                .build()
            file.writeTo(environment.codeGenerator, true, listOf(info.symbol.containingFile!!))
        }
    }

    private class SymbolInfo(
        val symbol: KSDeclaration,
        val type: Type,
        val widgetX: WidgetX,
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
            .appendLine("widgetX {")
            .appendLine("  name=${widgetX.name}")
            .appendLine("  cornerClip=${widgetX.cornerClip}")
            .appendLine("  windowFit=${widgetX.windowFit}")
            .appendLine("}")
            .toString()
    }
}