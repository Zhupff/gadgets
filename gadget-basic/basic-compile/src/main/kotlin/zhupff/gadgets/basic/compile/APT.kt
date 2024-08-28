package zhupff.gadgets.basic.compile

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

abstract class APT : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(elements: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean = false

    protected val Element.packageName: String; get() = processingEnv.elementUtils.getPackageOf(this).qualifiedName.toString()

    protected val Element.isClassElement: Boolean; get() = this::class.java.simpleName == "ClassSymbol"

    protected val Element.isVarElement: Boolean; get() = this::class.java.simpleName == "VarSymbol"
}