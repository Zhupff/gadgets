package zhupf.gadget.widget.dsl

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

class GadgetWidgetDslInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        DSL.init(context as Application)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}