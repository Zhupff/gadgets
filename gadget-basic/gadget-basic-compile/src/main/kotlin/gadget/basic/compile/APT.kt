package gadget.basic.compile

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

abstract class APT : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean = false

    protected val Element.declarePackageName: String
        get() = processingEnv.elementUtils.getPackageOf(this).qualifiedName.toString()

    protected val Element.declareClassName: String
        get() {
            val className = toString().replace(declarePackageName, "")
            return if (className.startsWith('.')) {
                className.substring(1)
            } else {
                className
            }
        }

    protected val Element.declareQualifiedName: String
        get() = toString()

    protected val Element.isClassSymbol: Boolean
        get() = this::class.java.simpleName == "ClassSymbol"

    protected val Element.isVarSymbol: Boolean
        get() = this::class.java.simpleName == "VarSymbol"
}