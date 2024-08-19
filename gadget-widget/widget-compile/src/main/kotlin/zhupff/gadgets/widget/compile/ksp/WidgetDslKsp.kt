package zhupff.gadgets.widget.compile.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import zhupff.gadgets.basic.compile.KSP

class WidgetDslKsp : KSP() {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        return emptyList()
    }
}