package gadget.gradle

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
                    val fileName = "${element.simpleName}Dependency"
                    val group = processingEnv.options["GROUP"] ?: throw IllegalArgumentException("GROUP Not Found!")
                    val version = processingEnv.options["VERSION"] ?: throw IllegalArgumentException("VERSION Not Found!")
                    add("${packageName}#${fileName}#${group}:%s:${version}")
                }
            } else {
                this.forEach { str ->
                    val (packageName, fileName, dependency) = str.split('#')
                    FileSpec.builder(packageName, fileName)
                        .addFunction(FunSpec.builder("dependency")
                            .addParameter(ParameterSpec("name", STRING))
                            .returns(STRING)
                            .addCode("return \"${dependency}\".format(name)")
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