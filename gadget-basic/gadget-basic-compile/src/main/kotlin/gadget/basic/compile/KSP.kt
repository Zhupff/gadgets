package gadget.basic.compile

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration

abstract class KSP : SymbolProcessorProvider, SymbolProcessor {

    protected lateinit var processingEnv: SymbolProcessorEnvironment; private set

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = apply {
        this.processingEnv = environment
    }

    override fun process(resolver: Resolver): List<KSAnnotated> = emptyList()

    protected val KSDeclaration.declarePackageName: String
        get() = "${packageName.getQualifier()}.${packageName.getShortName()}"

    protected val KSDeclaration.declareClassName: String
        get() {
            var name = simpleName.getShortName()
            var declaration = parentDeclaration
            while (declaration != null) {
                name = "${declaration.simpleName.getShortName()}.${name}"
                declaration = declaration.parentDeclaration
            }
            return name
        }

    protected val KSDeclaration.declareQualifiedName: String
        get() = "${declarePackageName}.${declareClassName}"
}