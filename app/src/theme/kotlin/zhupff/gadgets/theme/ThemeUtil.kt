package zhupff.gadgets.theme

import android.app.Activity
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mdc.hct.Hct
import zhupff.gadgets.basic.mutable
import java.io.FileOutputStream

object ThemeUtil {

    init {
        ThemeConfiguration
    }

    val LIGHT = ResourceTheme("Light", null, APP.resources)
    val DARK = ResourceVariantTheme("Dark", "dark", LIGHT)

    val current: LiveData<Theme>

    val assetsThemes: List<AssetsTheme> = listAssetsTheme()

    init {
        val isDarkSystem = isDarkSystem(APP.resources.configuration)
        current = MutableLiveData(if (isDarkSystem) DARK else LIGHT)
    }

    fun inject(activity: Activity) {
        val factory = activity.layoutInflater.factory2
        if (factory is ThemeFactory) return
        activity.layoutInflater.factory2 = ThemeFactory(factory, ThemeConfiguration)
    }

    fun switch(newTheme: Theme): Boolean {
        val currentTheme = current.value
        if (currentTheme !== newTheme) {
            current.mutable().postValue(newTheme)
            return true
        }
        return false
    }

    fun downloadFromAssets(name: String) {
        val assetsTheme = assetsThemes.find { it.name == name }
        if (assetsTheme == null || assetsTheme.theme != null) return

        val themepacksDir = APP.cacheDir.resolve("themepacks").also { dir ->
            if (!dir.exists()) {
                dir.mkdirs()
            }
        }
        val themeFile = themepacksDir.resolve(name)
        if (!themeFile.exists()) {
            APP.assets.open("themepacks/${name}").use { iStream ->
                FileOutputStream(themeFile).use { oStream ->
                    val buffer = ByteArray(2048)
                    while (true) {
                        val length = iStream.read(buffer)
                        if (length != -1) {
                            oStream.write(buffer, 0, length)
                        } else {
                            oStream.flush()
                            break
                        }
                    }
                }
            }
        }
        if (themeFile.exists()) {
            assetsTheme.theme = load(name, LIGHT, themeFile.path)
        }
    }

    fun load(name: String, parent: Theme, filePath: String): ResourceTheme? {
        val res = ResourceTheme.loadResources(filePath)
        if (res != null) {
            return ResourceTheme(name, parent, res)
        }
        return null
    }

    fun isDarkTheme(theme: Theme): Boolean {
        if (theme is ResourceTheme) {
            val surfaceColor = theme.getColorInt(R.color.theme__Surface)
            val hct = Hct.fromInt(surfaceColor)
            return hct.tone < 45.0
        }
        return false
    }

    private fun listAssetsTheme(): List<AssetsTheme> {
        val list = ArrayList<AssetsTheme>()
        val themepacksDir = APP.cacheDir.resolve("themepacks")
        APP.assets.list("themepacks")
            ?.forEach { name ->
                val assetsTheme = AssetsTheme(name)
                val file = themepacksDir.resolve(name)
                if (file.exists()) {
                    assetsTheme.theme = load(name, LIGHT, file.path)
                }
                list.add(assetsTheme)
            }
        return list
    }

    private fun isDarkSystem(configuration: Configuration): Boolean =
        (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}