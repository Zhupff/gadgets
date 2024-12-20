package zhupff.gadgets

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class GadgetNameProcessor : AbstractProcessor() {

    private val publications = HashMap<Element, Publication>(1)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(GadgetName::class.java.canonicalName)

    override fun process(typeElements: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.processingOver()) {
            publications.forEach { (element, publication) ->
                val packageName = processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString()
                val fileName = "${publication.gadget}Publication"
                FileSpec
                    .builder(packageName, fileName)
                    .addType(
                        TypeSpec
                        .objectBuilder(fileName)
                        .addProperty(
                            PropertySpec
                                .builder("GADGET", STRING, KModifier.CONST)
                                .initializer("\"${publication.gadget}\"")
                                .build()
                        )
                        .addProperty(
                            PropertySpec
                                .builder("NAME", STRING, KModifier.CONST)
                                .initializer("\"${publication.name}\"")
                                .build()
                        )
                        .addProperty(
                            PropertySpec
                                .builder("GROUP", STRING, KModifier.CONST)
                                .initializer("\"${publication.group}\"")
                                .build()
                        )
                        .addProperty(
                            PropertySpec
                                .builder("VERSION", STRING, KModifier.CONST)
                                .initializer("\"${publication.version}\"")
                                .build()
                        )
                        .addFunction(
                            FunSpec.builder("dependency")
                                .returns(STRING)
                                .addParameter(ParameterSpec("name", STRING))
                                .addCode("return \"${publication.group}:\${name}:${publication.version}\"")
                                .build()
                        )
                        .build()
                    )
                    .addFunction(
                        FunSpec.builder(publication.name)
                            .receiver(ClassName("zhupff.gadgets", "Gadgets"))
                            .addParameter(
                                ParameterSpec
                                    .builder("closure", LambdaTypeName.get(ClassName(packageName, publication.gadget), returnType = UNIT))
                                    .defaultValue("{}")
                                    .build()
                            )
                            .addStatement("val gadget = this[\"${publication.name}\"] as ${publication.gadget}")
                            .addStatement("gadget.beforeClosure()")
                            .addStatement("gadget.closure()")
                            .addStatement("gadget.afterClosure()")
                            .build()
                    )
                    .build()
                    .writeTo(processingEnv.filer)
            }
        } else {
            roundEnv.getElementsAnnotatedWith(GadgetName::class.java).forEach { element ->
                val gadget = element.simpleName.toString()
                val name = element.getAnnotation(GadgetName::class.java).value
                val group = processingEnv.options["GROUP"] ?: "GROUP_FAILURE"
                val version = processingEnv.options["VERSION"] ?: "VERSION_FAILURE"
                publications[element] = Publication(gadget, name, group, version)
            }
        }
        return false
    }

    private class Publication(
        val gadget: String,
        val name: String,
        val group: String,
        val version: String,
    )
}