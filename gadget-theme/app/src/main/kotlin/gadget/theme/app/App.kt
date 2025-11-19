package gadget.theme.app

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gadget.basic.saveTo
import gadget.theme.ResourcesTheme
import gadget.theme.ResourcesVariantTheme
import gadget.theme.Theme
import gadget.theme.ThemeObservable
import java.io.File

class App : Application(), ThemeObservable {

    companion object {

        lateinit var INSTANCE: App
            private set
    }

    val lightTheme: ResourcesTheme by lazy {
        ResourcesTheme("Light", null, packageName, resources)
    }
    val nightTheme: ResourcesVariantTheme by lazy {
        ResourcesVariantTheme("Night", lightTheme, "night")
    }
    val otherTheme = ArrayList<Theme>()

    private val themeObservable = MutableLiveData<Theme>()

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        themeObservable.value = if (isNightMode) nightTheme else lightTheme
        val themeDir = cacheDir.resolve("theme").also(File::mkdirs)
        assets.list("theme")?.forEach { name ->
            val file = themeDir.resolve(name)
            if (!file.exists()) {
                assets.open("theme/${name}").saveTo(file)
            }
            if (file.exists()) {
                ResourcesTheme.load1(this, file, name, lightTheme)?.let {
                    otherTheme.add(it)
                }
            }
        }
    }

    override fun subscribe(): LiveData<Theme> = themeObservable

    fun switch(newTheme: Theme) {
        if (themeObservable.value != newTheme) {
            themeObservable.postValue(newTheme)
        }
    }
}