package gadget.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.STRING
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class GadgetEx(val value: String = "") {

    class Processor : AbstractProcessor(), MutableList<String> by ArrayList() {

        override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

        override fun getSupportedAnnotationTypes(): Set<String> = setOf(GadgetEx::class.java.canonicalName)

        override fun process(typeElements: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
            if (!roundEnv.processingOver()) {
                roundEnv.getElementsAnnotatedWith(GadgetEx::class.java).forEach { element ->
                    val packageName = processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString()
                    val className = element.simpleName
                    val group = processingEnv.options["GROUP"] ?: throw IllegalArgumentException("GROUP Not Found!")
                    val version = processingEnv.options["VERSION"] ?: throw IllegalArgumentException("VERSION Not Found!")
                    add("${packageName}#${className}#${group}#${version}")
                }
            } else {
                this.forEach { str ->
                    val (packageName, className, group, version) = str.split('#')
                    FileSpec.builder(packageName, className + "Dependency")
                        .addFunction(FunSpec.builder("dependency")
                            .receiver(ClassName(packageName, className))
                            .addParameter(ParameterSpec("name", STRING))
                            .returns(STRING)
                            .addCode("return \"${group}:\${name}:${version}\"")
                            .build()
                        )
                        .build()
                        .writeTo(processingEnv.filer)
                }
            }
            return false
        }
    }
}