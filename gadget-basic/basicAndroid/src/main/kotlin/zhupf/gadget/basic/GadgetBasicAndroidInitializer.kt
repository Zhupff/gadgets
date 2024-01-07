package zhupf.gadget.basic

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

class GadgetBasicAndroidInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Basic.init(context as Application)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}