package zhupff.gadgets.logger

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

internal lateinit var APPLICATION: Application; private set

class ApiInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        APPLICATION = context as Application
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}