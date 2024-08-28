package zhupff.gadgets.theme.compile.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ksp.writeTo
import zhupff.gadgets.basic.compile.KSP
import zhupff.gadgets.theme.ThemeAttributeDsl
import zhupff.gadgets.theme.compile.processor.ThemeAttributeDslProcessor

class ThemeAttributeDslKsp : KSP() {

    private val symbol2item = ArrayList<Pair<KSDeclaration, ThemeAttributeDslProcessor.Item>>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(ThemeAttributeDsl::class.java.canonicalName).forEach { symbol ->
            if (symbol is KSDeclaration) {
                val alias = symbol.annotations.find {
                    it.annotationType.resolve().declaration.declareQualifiedName == ThemeAttributeDsl::class.java.canonicalName
                }?.arguments?.find {
                    it.name?.getShortName() == "alias"
                }?.value as? String
                if (alias.isNullOrEmpty()) return@forEach
                val canonicalName = when (symbol) {
                    is KSClassDeclaration -> symbol.declareQualifiedName
                    is KSPropertyDeclaration -> symbol.type.resolve().declaration.declareQualifiedName
                    else -> null
                }
                if (canonicalName.isNullOrEmpty()) return@forEach
                val packageName = symbol.declarePackageName
                symbol2item.add(symbol to ThemeAttributeDslProcessor.Item(alias, packageName, canonicalName))
            }
        }
        return emptyList()
    }

    override fun finish() {
        symbol2item.forEach { (symbol, item) ->
            ThemeAttributeDslProcessor.buildFileSpec(item).writeTo(processingEnv.codeGenerator, true, listOf(symbol.containingFile!!))
        }
    }
}