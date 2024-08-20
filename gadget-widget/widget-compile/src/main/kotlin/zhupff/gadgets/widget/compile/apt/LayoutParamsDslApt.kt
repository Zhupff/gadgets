package zhupff.gadgets.widget.compile.apt

import zhupff.gadgets.basic.compile.APT
import zhupff.gadgets.widget.LayoutParamsDsl
import zhupff.gadgets.widget.compile.processor.LayoutParamsDslProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

class LayoutParamsDslApt : APT() {

    private val items = HashSet<LayoutParamsDslProcessor.Item>()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(LayoutParamsDsl::class.java.canonicalName)

    override fun process(elements: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        if (roundEnv?.processingOver() != true) {
            roundEnv?.getElementsAnnotatedWith(LayoutParamsDsl::class.java)
                ?.forEach { element ->
                    val layoutParamsDsl = element.getAnnotation(LayoutParamsDsl::class.java)
                    if (element.isClassElement) {
                        items.add(LayoutParamsDslProcessor.Item(layoutParamsDsl.alias, element.packageName, element.toString()))
                    } else if (element.isVarElement) {
                        items.add(LayoutParamsDslProcessor.Item(layoutParamsDsl.alias, element.packageName, element.asType().toString()))
                    }
                }
        } else {
            for (item in items) {
                LayoutParamsDslProcessor.buildFileSpec(item).writeTo(processingEnv.filer)
            }
        }
        return false
    }
}