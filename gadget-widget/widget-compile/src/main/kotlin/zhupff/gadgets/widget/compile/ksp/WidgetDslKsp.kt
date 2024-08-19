package zhupff.gadgets.widget.compile.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ksp.writeTo
import zhupff.gadgets.basic.compile.KSP
import zhupff.gadgets.widget.WidgetDsl
import zhupff.gadgets.widget.compile.processor.WidgetDslProcessor

class WidgetDslKsp : KSP() {

    private val symbol2items = ArrayList<Pair<KSDeclaration, WidgetDslProcessor.Item>>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(WidgetDsl::class.java.canonicalName).forEach { symbol ->
            if (symbol is KSDeclaration) {
                val alias = symbol.annotations.find {
                    it.annotationType.resolve().declaration.declareQualifiedName == WidgetDsl::class.java.canonicalName
                }?.arguments?.let { arguments ->
                    arguments.find {
                        it.name?.getShortName() == "alias"
                    }?.value as? String
                }
                if (alias.isNullOrEmpty()) return@forEach
                val canonicalName = when (symbol) {
                    is KSClassDeclaration -> symbol.declareQualifiedName
                    is KSPropertyDeclaration -> {
                        symbol.type.resolve().declaration.declareQualifiedName
                    }
                    else -> null
                }
                if (canonicalName.isNullOrEmpty()) return@forEach
                val packageName = symbol.declarePackageName
                val item = WidgetDslProcessor.Item(alias, packageName, canonicalName)
                symbol2items.add(symbol to item)
            }
        }
        return emptyList()
    }

    override fun finish() {
        symbol2items.forEach { (symbol, item) ->
            WidgetDslProcessor.buildFileSpec(item).writeTo(processingEnv.codeGenerator, true, listOf(symbol.containingFile!!))
        }
    }
}