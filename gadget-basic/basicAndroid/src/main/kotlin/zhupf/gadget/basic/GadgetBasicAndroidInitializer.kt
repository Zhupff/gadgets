package zhupf.gadget.basic

import android.content.Context
import androidx.startup.Initializer

class GadgetBasicAndroidInitializer : Initializer<Unit> {

    override fun create(context: Context) {
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}