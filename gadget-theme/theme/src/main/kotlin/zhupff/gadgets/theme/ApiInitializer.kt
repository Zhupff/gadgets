package zhupff.gadgets.theme

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

internal lateinit var APPLICATION: Application; private set

class ApiInitializer internal constructor(): Initializer<Unit> {

    override fun create(context: Context) {
        APPLICATION = context.applicationContext as Application
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}