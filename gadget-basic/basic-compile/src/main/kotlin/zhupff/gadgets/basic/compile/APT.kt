package zhupff.gadgets.basic.compile

import javax.annotation.processing.AbstractProcessor
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element

abstract class APT : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    protected val Element.packageName: String; get() = processingEnv.elementUtils.getPackageOf(this).qualifiedName.toString()

    protected val Element.isClassElement: Boolean; get() = this::class.java.simpleName == "ClassSymbol"

    protected val Element.isVarElement: Boolean; get() = this::class.java.simpleName == "VarSymbol"
}