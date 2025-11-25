package gadget.widget

import com.google.devtools.ksp.processing.Resolver
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
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.writeTo
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

internal object WidgetDSLProcessor {

    class APT : gadget.basic.compile.APT(), MutableList<C> by ArrayList() {

        override fun getSupportedAnnotationTypes(): Set<String> = setOf(WidgetDSL::class.java.canonicalName)

        override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
            if (!roundEnv.processingOver()) {
                roundEnv.getElementsAnnotatedWith(WidgetDSL::class.java).forEach { element ->
                    if (element is TypeElement) {
                        add(C(element.declarePackageName, element.declareClassName))
                    } else if (element is VariableElement) {
                        val type = processingEnv.elementUtils.getTypeElement(element.asType().toString())
                        add(C(type.declarePackageName, type.declareClassName))
                    }
                }
            } else {
                forEach { buildFileSpec(it).writeTo(processingEnv.filer) }
            }
            return false
        }
    }

    class KSP : gadget.basic.compile.KSP(), MutableList<Pair<KSDeclaration, C>> by ArrayList() {

        override fun process(resolver: Resolver): List<KSAnnotated> {
            resolver.getSymbolsWithAnnotation(WidgetDSL::class.java.canonicalName).forEach { symbol ->
                if (symbol is KSDeclaration) {
                    if (symbol is KSClassDeclaration) {
                        add(symbol to C(symbol.declarePackageName, symbol.declareClassName))
                    } else if (symbol is KSPropertyDeclaration) {
                        val type = symbol.type.resolve().declaration
                        add(symbol to C(type.declarePackageName, type.declareClassName))
                    }
                }
            }
            return emptyList()
        }

        override fun finish() {
            forEach { (symbol, target) ->
                buildFileSpec(target).writeTo(processingEnv.codeGenerator, true, listOf(symbol.containingFile!!))
            }
        }
    }

    private data class C(
        val packageName: String,
        val className: String,
    )

    private fun buildFileSpec(target: C): FileSpec {
        val targetClassName = ClassName(target.packageName, target.className)
        val contextClassName = ClassName("", "android.content.Context")
        val viewClassName = ClassName("", "android.view.View")
        val viewGroupClassName = ClassName("", "android.view.ViewGroup")
        val layoutParamsClassName = ClassName("", "android.view.ViewGroup.LayoutParams")
        val resourcesCompatClassName = ClassName("", "androidx.core.content.res.ResourcesCompat")
        val contextParameter = ParameterSpec("context", contextClassName)
        val idStringParameter = ParameterSpec.builder("id", STRING.copy(nullable = true)).defaultValue("null").build()
        val sizeParameter = ParameterSpec("size", Pair::class.asClassName().parameterizedBy(INT, INT))
        val layoutParamsParameter = ParameterSpec("layoutParams", layoutParamsClassName)
        val indexParameter = ParameterSpec.builder("index", INT).defaultValue("-1").build()
        val lambdaParameter: ParameterSpec = ParameterSpec.builder(
            "lambda",
            LambdaTypeName.get(
                receiver = targetClassName.copy(annotations = listOf(AnnotationSpec.builder(DSL::class).build())),
                parameters = listOf(ParameterSpec.unnamed(targetClassName)),
                returnType = UNIT,
            ),
        ).build()

        return FileSpec.builder(target.packageName, target.className.replace('.', '_') + "_WIDGETDSL")
            .addImport(contextClassName, "")
            .addImport(viewClassName, "")
            .addImport(viewGroupClassName, "")
            .addImport(layoutParamsClassName, "")
            .addImport(resourcesCompatClassName, "")
            .addImport("gadget.widget", "VIEW_ID")
            .addFunction(
                FunSpec.builder(target.className.replace(".", ""))
                    .addModifiers(KModifier.INLINE)
                    .addParameter(contextParameter)
                    .addParameter(idStringParameter)
                    .addParameter(sizeParameter)
                    .addParameter(lambdaParameter)
                    .returns(targetClassName)
                    .addCode("""
                        return ${target.packageName}.${target.className}(context).also { view ->
                          if (id != null) {
                            view.id = id.VIEW_ID
                          }
                          view.layoutParams = LayoutParams(size.first, size.second)
                          view.lambda(view)
                        }
                    """.trimIndent())
                    .build()
            )
            .addFunction(
                FunSpec.builder(target.className.replace(".", ""))
                    .addModifiers(KModifier.INLINE)
                    .addParameter(contextParameter)
                    .addParameter(idStringParameter)
                    .addParameter(layoutParamsParameter)
                    .addParameter(lambdaParameter)
                    .returns(targetClassName)
                    .addCode("""
                        return ${target.packageName}.${target.className}(context).also { view ->
                          if (id != null) {
                            view.id = id.VIEW_ID
                          }
                          view.layoutParams = layoutParams
                          view.lambda(view)
                        }
                    """.trimIndent())
                    .build()
            )
            .addFunction(
                FunSpec.builder(target.className.replace(".", ""))
                    .addModifiers(KModifier.INLINE)
                    .receiver(viewGroupClassName)
                    .addParameter(idStringParameter)
                    .addParameter(sizeParameter)
                    .addParameter(indexParameter)
                    .addParameter(lambdaParameter)
                    .returns(targetClassName)
                    .addCode("""
                        return ${target.packageName}.${target.className}(context).also { view ->
                          if (id != null) {
                            view.id = id.VIEW_ID
                          }
                          this.addView(view, index, LayoutParams(size.first, size.second))
                          view.lambda(view)
                        }
                    """.trimIndent())
                    .build()
            )
            .addFunction(
                FunSpec.builder(target.className.replace(".", ""))
                    .addModifiers(KModifier.INLINE)
                    .receiver(viewGroupClassName)
                    .addParameter(idStringParameter)
                    .addParameter(layoutParamsParameter)
                    .addParameter(indexParameter)
                    .addParameter(lambdaParameter)
                    .returns(targetClassName)
                    .addCode("""
                        return ${target.packageName}.${target.className}(context).also { view ->
                          if (id != null) {
                            view.id = id.VIEW_ID
                          }
                          this.addView(view, index, layoutParams)
                          view.lambda(view)
                        }
                    """.trimIndent())
                    .build()
            )
            .build()
    }
}