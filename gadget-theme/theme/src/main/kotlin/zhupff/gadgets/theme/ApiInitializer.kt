package zhupff.gadgets.theme

import android.content.Context
import androidx.startup.Initializer

class ApiInitializer : Initializer<Unit> {

    override fun create(context: Context) {
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}