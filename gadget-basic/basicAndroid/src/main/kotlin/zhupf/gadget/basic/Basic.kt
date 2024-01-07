package zhupf.gadget.basic

import android.app.Application

object Basic {

    lateinit var application: Application; private set

    var density: Float = 0F
        get() {
            if (field <= 0F) {
                field = application.resources.displayMetrics.density
            }
            return field
        }
        private set

    fun init(application: Application) {
        this.application = application
    }
}