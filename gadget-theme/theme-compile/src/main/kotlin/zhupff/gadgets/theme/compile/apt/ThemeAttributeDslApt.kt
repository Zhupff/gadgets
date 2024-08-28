package zhupff.gadgets.theme.compile.apt

import zhupff.gadgets.basic.compile.APT
import zhupff.gadgets.theme.ThemeAttributeDsl
import zhupff.gadgets.theme.compile.processor.ThemeAttributeDslProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

class ThemeAttributeDslApt : APT() {

    private val items = HashSet<ThemeAttributeDslProcessor.Item>()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(ThemeAttributeDsl::class.java.canonicalName)

    override fun process(elements: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        if (roundEnv?.processingOver() != true) {
            roundEnv?.getElementsAnnotatedWith(ThemeAttributeDsl::class.java)
                ?.forEach { element ->
                    val themeAttributeDsl = element.getAnnotation(ThemeAttributeDsl::class.java)
                    if (element.isClassElement) {
                        items.add(ThemeAttributeDslProcessor.Item(themeAttributeDsl.alias, element.packageName, element.toString()))
                    } else if (element.isVarElement) {
                        items.add(ThemeAttributeDslProcessor.Item(themeAttributeDsl.alias, element.packageName, element.asType().toString()))
                    }
                }
        } else {
            for (item in items) {
                ThemeAttributeDslProcessor.buildFileSpec(item).writeTo(processingEnv.filer)
            }
        }
        return false
    }
}