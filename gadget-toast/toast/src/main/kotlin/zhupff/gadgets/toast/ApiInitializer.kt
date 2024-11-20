package zhupff.gadgets.toast

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

internal lateinit var APPLICATION: Application; private set

class ApiInitializer internal constructor(): Initializer<Unit> {

    override fun create(context: Context) {
        APPLICATION = context as Application
        Toaster // init
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}