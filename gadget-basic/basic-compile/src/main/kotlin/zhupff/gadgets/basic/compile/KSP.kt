package zhupff.gadgets.basic.compile

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

abstract class KSP : SymbolProcessorProvider, SymbolProcessor {

    protected lateinit var processingEnv: SymbolProcessorEnvironment; private set

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = apply {
        this.processingEnv = environment
    }
}