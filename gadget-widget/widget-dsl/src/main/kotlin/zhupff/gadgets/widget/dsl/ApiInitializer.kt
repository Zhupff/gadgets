package zhupff.gadgets.widget.dsl

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.startup.Initializer

internal lateinit var APPLICATION: Application; private set
internal val RESOURCES: Resources by lazy { APPLICATION.resources }

class ApiInitializer internal constructor(): Initializer<Unit> {

    override fun create(context: Context) {
        APPLICATION = context as Application
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}