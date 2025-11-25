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
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.ksp.writeTo
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

internal object LayoutParamsDSLProcessor {

    class APT : gadget.basic.compile.APT(), MutableList<C> by ArrayList() {

        override fun getSupportedAnnotationTypes(): Set<String> = setOf(LayoutParamsDSL::class.java.canonicalName)

        override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
            if (!roundEnv.processingOver()) {
                roundEnv.getElementsAnnotatedWith(LayoutParamsDSL::class.java).forEach { element ->
                    val layoutParamsDsl = element.getAnnotation(LayoutParamsDSL::class.java)
                    if (element is TypeElement) {
                        add(C(layoutParamsDsl.alias, element.declarePackageName, element.declareClassName))
                    } else if (element is VariableElement) {
                        val type = processingEnv.elementUtils.getTypeElement(element.asType().toString())
                        add(C(layoutParamsDsl.alias, type.declarePackageName, type.declareClassName))
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
            resolver.getSymbolsWithAnnotation(LayoutParamsDSL::class.java.canonicalName).forEach { symbol ->
                if (symbol is KSDeclaration) {
                    val alias = symbol.annotations.find {
                        it.annotationType.resolve().declaration.declareQualifiedName == LayoutParamsDSL::class.java.canonicalName
                    }?.arguments?.find {
                        it.name?.getShortName() == "alias"
                    }?.value as String
                    if (symbol is KSClassDeclaration) {
                        add(symbol to C(alias, symbol.declarePackageName, symbol.declareClassName))
                    } else if (symbol is KSPropertyDeclaration) {
                        val type = symbol.type.resolve().declaration
                        add(symbol to C(alias, type.declarePackageName, type.declareClassName))
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
        val alias: String,
        val packageName: String,
        val className: String,
    )

    private fun buildFileSpec(target: C): FileSpec {
        val targetClassName = ClassName(target.packageName, target.className)
        val viewClassName = ClassName("", "android.view.View")
        val viewGroupClassName = ClassName("", "android.view.ViewGroup")
        val lambdaParameter: ParameterSpec = ParameterSpec.builder(
            "lambda",
            LambdaTypeName.get(
                receiver = targetClassName.copy(annotations = listOf(AnnotationSpec.builder(DSL::class).build())),
                returnType = UNIT,
            ),
        ).build()

        return FileSpec.builder(target.packageName, target.className.replace('.', '_') + "_LAYOUTPARAMSDSL")
            .addImport(viewClassName, "")
            .addImport(viewGroupClassName, "")
            .addTypeAlias(TypeAliasSpec.builder(target.alias, targetClassName).build())
            .addFunction(
                FunSpec.builder(target.alias)
                    .addModifiers(KModifier.INLINE)
                    .receiver(viewClassName)
                    .addParameter(lambdaParameter)
                    .returns(targetClassName)
                    .addCode("""
                        val lp: ${target.alias} = if (this.layoutParams is ${target.alias}) {
                          this.layoutParams as ${target.alias}
                        } else if (this.layoutParams == null) {
                          if (this is ViewGroup) {
                            ${target.alias}(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                          } else {
                            ${target.alias}(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                          }
                        } else {
                          ${target.alias}(this.layoutParams)
                        }.also(lambda)
                        this.layoutParams = lp
                        return lp
                    """.trimIndent())
                    .build()
            )
            .addFunction(
                FunSpec.builder(target.alias)
                    .addModifiers(KModifier.INLINE)
                    .receiver(viewClassName)
                    .addParameter(
                        ParameterSpec.builder(
                            "def",
                            LambdaTypeName.get(
                                parameters = listOf(ParameterSpec.unnamed(viewClassName)),
                                returnType = targetClassName,
                            ),
                        ).build()
                    )
                    .addParameter(lambdaParameter)
                    .returns(targetClassName)
                    .addCode("""
                        val lp: ${target.alias} = if (this.layoutParams is ${target.alias}) {
                          this.layoutParams as ${target.alias}
                        } else {
                          def(this)
                        }.also(lambda)
                        this.layoutParams = lp
                        return lp
                    """.trimIndent())
                    .build()
            )
            .build()
    }
}