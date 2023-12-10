package zhupf.gadget.toast

import android.content.Context
import androidx.startup.Initializer

class GadgetToastApiInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Toaster.init(context.applicationContext)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}