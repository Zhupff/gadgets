package zhupff.gadgets.theme

import android.app.Application
import android.content.res.AssetManager
import android.content.res.Resources
import java.io.File

class APP : Application() {

    companion object {
        private lateinit var INSTANCE: APP

        val resources: Resources; get() = INSTANCE.resources
        val cacheDir: File; get() = INSTANCE.cacheDir
        val assets: AssetManager; get() = INSTANCE.assets
    }

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        ThemeUtil
    }
}