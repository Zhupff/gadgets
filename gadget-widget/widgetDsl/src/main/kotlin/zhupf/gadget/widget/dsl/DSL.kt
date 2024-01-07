package zhupf.gadget.widget.dsl

import android.app.Application
import android.content.res.Resources

object DSL {

    lateinit var application: Application; private set

    val resources: Resources by lazy { application.resources }

    fun init(application: Application) {
        this.application = application
    }
}