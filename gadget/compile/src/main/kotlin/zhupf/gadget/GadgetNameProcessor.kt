package zhupf.gadget

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

    override fun getSupportedAnnotationTypes(): Set<String> =
        setOf(GadgetName::class.java.canonicalName)

    override fun process(typeElements: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.processingOver()) {
            publications.forEach { (element, publication) ->
                val packageName = processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString()
                val fileName = "${publication.gadget}Publication"
                FileSpec
                    .builder(packageName, fileName)
                    .addType(TypeSpec
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
                                .builder("ARTIFACT", STRING, KModifier.CONST)
                                .initializer("\"${publication.artifact}\"")
                                .build()
                        )
                        .addProperty(
                            PropertySpec
                                .builder("VERSION", STRING, KModifier.CONST)
                                .initializer("\"${publication.version}\"")
                                .build()
                        )
                        .build()
                    )
                    .addFunction(
                        FunSpec.builder(publication.name)
                            .receiver(ClassName("zhupf.gadget", "GadgetExtension"))
                            .addParameter(
                                ParameterSpec
                                    .builder("closure", LambdaTypeName.get(ClassName(packageName, publication.gadget), returnType = UNIT))
                                    .defaultValue("{}")
                                    .build()
                            )
                            .addStatement("val gadget = gadgets[\"${publication.name}\"] as GadgetBasic")
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
                if (System.getenv("JITPACK").toBoolean()) {
                    val group = System.getenv("GROUP") ?: ""
                    val artifact = System.getenv("ARTIFACT") ?: ""
                    val version = System.getenv("VERSION") ?: ""
                    publications[element] = Publication(gadget, name, group, artifact, version)
                } else {
                    publications[element] =
                        Publication(gadget, name, "group", "artifact", "version")
                }
            }
        }
        return false
    }

    private class Publication(
        val gadget: String,
        val name: String,
        val group: String,
        val artifact: String,
        val version: String,
    ) {
        override fun toString(): String = "$gadget-$name $group:$artifact:$version"
    }
}