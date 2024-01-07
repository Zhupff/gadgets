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
                val widgetX = symbol.annotations.find {
                    it.annotationType.resolve().declaration.declareQualifiedName == WidgetX::class.java.canonicalName
                }?.arguments?.let { arguments ->
                    val alias = arguments.find {
                        it.name?.getShortName() == "alias"
                    }?.value as? String
                    val qualifiedName = arguments.find {
                        it.name?.getShortName() == "qualifiedName"
                    }?.value as? String ?: ""
                    val cornerClip = arguments.find {
                        it.name?.getShortName() == "cornerClip"
                    }?.value as? Boolean ?: false
                    val windowFit = arguments.find {
                        it.name?.getShortName() == "windowFit"
                    }?.value as? Boolean ?: false
                    if (alias.isNullOrEmpty()) {
                        return@forEach
                    }
                    WidgetX(alias, qualifiedName, cornerClip, windowFit)
                } ?: return@forEach
                val qualifiedName = if (widgetX.qualifiedName.isNullOrEmpty()) {
                    when (symbol) {
                        is KSClassDeclaration -> symbol.declareQualifiedName
                        is KSPropertyDeclaration -> {
                            symbol.type.resolve().declaration.let { declaration ->
                                if (declaration is KSClassDeclaration && declaration.qualifiedName != null) {
                                    declaration.declareQualifiedName
                                } else {
                                    widgetX.qualifiedName
                                }
                            }
                        }
                        else -> return@forEach
                    }
                } else {
                    widgetX.qualifiedName
                }
                symbols.add(SymbolInfo(
                    symbol,
                    qualifiedName,
                    widgetX,
                ))
            }
        }
        return emptyList()
    }

    override fun finish() {
        symbols.forEach { info ->
            val file = FileSpec.builder(info.symbol.declarePackageName, info.widgetX.alias)
//                .addFileComment(info.toString())
                .addType(
                    TypeSpec.classBuilder(info.widgetX.alias)
                        .addModifiers(KModifier.OPEN)
                        .superclass(ClassName("", info.qualifiedName))
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
        val qualifiedName: String,
        val widgetX: WidgetX,
    ) {
        override fun toString(): String = StringBuilder()
            .appendLine("symbol=$symbol")
            .appendLine("qualifiedName=$qualifiedName")
            .appendLine("widgetX {")
            .appendLine("  name=${widgetX.alias}")
            .appendLine("  qualifiedName=${widgetX.qualifiedName}")
            .appendLine("  cornerClip=${widgetX.cornerClip}")
            .appendLine("  windowFit=${widgetX.windowFit}")
            .appendLine("}")
            .toString()
    }
}