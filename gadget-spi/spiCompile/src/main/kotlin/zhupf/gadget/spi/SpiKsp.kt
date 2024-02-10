package zhupf.gadget.spi

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType

class SpiKsp : SymbolProcessorProvider, SymbolProcessor {

    private lateinit var environment: SymbolProcessorEnvironment

    private val symbols = HashMap<String, HashSet<SymbolInfo>>()

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = apply {
        this.environment = environment
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(SpiAnnotation::class.java.canonicalName).mapNotNull {
            it as? KSDeclaration
        }.forEach { symbol ->
            symbol.annotations.find {
                it.annotationType.resolve().declaration.declareQualifiedName == SpiAnnotation::class.java.canonicalName
            }?.arguments?.find {
                it.name?.getShortName() == "cls"
            }?.value.let {
                it as? Iterable<*>
            }?.mapNotNull {
                it as? KSType
            }?.forEach { cls ->
                symbols.getOrPut(cls.declaration.declareQualifiedName) {
                    HashSet()
                }.add(SymbolInfo(symbol))
            }
        }
        return emptyList()
    }

    override fun finish() {
        super.finish()
        symbols.forEach { name, symbols ->
            try {
                environment.codeGenerator.createNewFile(
                    Dependencies(true, *symbols.mapNotNull { it.symbol.containingFile }.toTypedArray()),
                    "", "META-INF/services/${name}", ""
                ).bufferedWriter(Charsets.UTF_8).use { writer ->
                    symbols.forEach {
                        writer.write(it.symbol.declareQualifiedName)
                        writer.newLine()
                    }
                    writer.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private class SymbolInfo(
        val symbol: KSDeclaration,
    ) {
        override fun hashCode(): Int = symbol.hashCode()

        override fun equals(other: Any?): Boolean = symbol == other
    }
}