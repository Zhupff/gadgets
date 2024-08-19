package zhupff.gadgets.widget.compile.apt

import zhupff.gadgets.basic.compile.APT
import zhupff.gadgets.widget.WidgetDsl
import zhupff.gadgets.widget.compile.processor.WidgetDslProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

class WidgetDslApt : APT() {

    private val items = HashSet<WidgetDslProcessor.Item>()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(WidgetDsl::class.java.canonicalName)

    override fun process(elements: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        if (roundEnv?.processingOver() != true) {
            roundEnv?.getElementsAnnotatedWith(WidgetDsl::class.java)
                ?.forEach { element ->
                    val widgetDsl = element.getAnnotation(WidgetDsl::class.java)
                    if (element.isClassElement) {
                        items.add(WidgetDslProcessor.Item(widgetDsl.alias, element.packageName, element.toString()))
                    } else if (element.isVarElement) {
                        items.add(WidgetDslProcessor.Item(widgetDsl.alias, element.packageName, element.asType().toString()))
                    }
                }
        } else {
            for (item in items) {
                WidgetDslProcessor.buildFileSpec(item).writeTo(processingEnv.filer)
            }
        }
        return false
    }
}