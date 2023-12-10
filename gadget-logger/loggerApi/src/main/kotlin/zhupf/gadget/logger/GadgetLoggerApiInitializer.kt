package zhupf.gadget.logger

import android.content.Context
import androidx.startup.Initializer

class GadgetLoggerApiInitializer : Initializer<Unit> {

    override fun create(context: Context) {
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}