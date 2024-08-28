package zhupff.gadgets.theme

import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import org.json.JSONObject

class Theme(
    val resources: Resources,
    val parent: Theme?,
) : MutableMap<String, Any> by HashMap(4) {

    companion object {
        const val START_VERSION = -1
        const val INFO_THEME_ID = "theme_id"
        const val INFO_THEME_NAME_CN = "theme_name_cn"
        const val INFO_THEME_NAME_EN = "theme_name_en"
        const val INFO_IS_ORIGIN = "is_origin"
        const val INFO_IS_DARK = "is_dark"

        fun getInfoInResources(res: Resources): JSONObject? {
            return try {
                JSONObject(res.assets.open("theme.json").reader(Charsets.UTF_8).readText())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    interface Callback {
        fun onVersionChanged(theme: Theme, oldVersion: Int, newVersion: Int)
    }

    init {
        parent?.children?.add(this)
        parent?.addCallback(object : Callback {
            override fun onVersionChanged(theme: Theme, oldVersion: Int, newVersion: Int) {
                upgradeVersion()
            }
        })
    }

    val info: JSONObject = try {
        JSONObject(resources.assets.open("theme.json").reader(Charsets.UTF_8).readText())
    } catch (e: Exception) {
        e.printStackTrace()
        JSONObject()
    }

    val themeId: String = info.getString(INFO_THEME_ID)

    val themeNameCN: String = info.optString(INFO_THEME_NAME_CN, "")

    val themeNameEN: String = info.optString(INFO_THEME_NAME_EN, "")

    val isOrigin: Boolean = info.optBoolean(INFO_IS_ORIGIN, false)

    val isDark: Boolean = info.optBoolean(INFO_IS_DARK, false)

    private val children by lazy { ArrayList<Theme>(2) }

    var version: Int = START_VERSION + 1
        private set(value) {
            if (field != value) {
                val oldVersion = field
                field = value
                callbacks.forEach { it.onVersionChanged(this, oldVersion, value) }
            }
        }

    fun upgradeVersion(): Int = ++version

    private val callbacks = LinkedHashSet<Callback>(2)

    fun addCallback(callback: Callback) = callbacks.add(callback)

    fun removeCallback(callback: Callback) = callbacks.remove(callback)

    private val cacheIds by lazy { HashMap<Int, Int>() }

    override fun toString(): String = info.toString()

    fun loadThemeResources(filePath: String): Resources? =
        try {
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPath = assetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assetManager, filePath)
            Resources(assetManager, resources.displayMetrics, resources.configuration)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    @AnyRes
    fun getResourceIdentifier(@AnyRes originId: Int): Int {
        if (isOrigin || originId == ResourcesCompat.ID_NULL) return originId
        return cacheIds.getOrPut(originId) {
            var p = parent
            while (p != null && p != p.parent && !p.isOrigin) {
                p = p.parent
            }
            if (p?.isOrigin == true) {
                try {
                    val name = p.resources.getResourceEntryName(originId)
                    val type = p.resources.getResourceTypeName(originId)
                    resources.getIdentifier(name, type, themeId)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ResourcesCompat.ID_NULL
                }
            } else {
                ResourcesCompat.ID_NULL
            }
        }
    }

    @AnyRes
    fun getResourceIdentifier(@AnyRes originId: Int, originName: String, originType: String): Int {
        if (isOrigin || originId == ResourcesCompat.ID_NULL) return originId
        return cacheIds.getOrPut(originId) {
            try {
                resources.getIdentifier(originName, originType, themeId)
            } catch (e: Exception) {
                e.printStackTrace()
                ResourcesCompat.ID_NULL
            }
        }
    }

    @ColorInt
    fun getColor(@ColorRes originId: Int): Int {
        val id = getResourceIdentifier(originId)
        return if (id != ResourcesCompat.ID_NULL) {
            ResourcesCompat.getColor(resources, id, null)
        } else {
            parent?.getColor(originId)!!
        }
    }

    @ColorInt
    fun getColor(@ColorRes originId: Int, originName: String, originType: String): Int {
        val id = getResourceIdentifier(originId, originName, originType)
        return if (id != ResourcesCompat.ID_NULL) {
            ResourcesCompat.getColor(resources, id, null)
        } else {
            parent?.getColor(originId, originName, originType)!!
        }
    }

    fun getColorStateList(@ColorRes originId: Int): ColorStateList? {
        val id = getResourceIdentifier(originId)
        return if (id != ResourcesCompat.ID_NULL) {
            ResourcesCompat.getColorStateList(resources, id, null)
        } else {
            parent?.getColorStateList(originId)
        }
    }

    fun getColorStateList(@ColorRes originId: Int, originName: String, originType: String): ColorStateList? {
        val id = getResourceIdentifier(originId, originName, originType)
        return if (id != ResourcesCompat.ID_NULL) {
            ResourcesCompat.getColorStateList(resources, id, null)
        } else {
            parent?.getColorStateList(originId, originName, originType)
        }
    }

    fun getDrawable(@DrawableRes originId: Int): Drawable? {
        val id = getResourceIdentifier(originId)
        return if (id != ResourcesCompat.ID_NULL) {
            ResourcesCompat.getDrawable(resources, id, null)
        } else {
            parent?.getDrawable(originId)
        }
    }

    fun getDrawable(@DrawableRes originId: Int, originName: String, originType: String): Drawable? {
        val id = getResourceIdentifier(originId, originName, originType)
        return if (id != ResourcesCompat.ID_NULL) {
            ResourcesCompat.getDrawable(resources, id, null)
        } else {
            parent?.getDrawable(originId, originName, originType)
        }
    }

    fun getString(@StringRes originId: Int, vararg formatArgs: Any): String {
        val id = getResourceIdentifier(originId)
        return if (id != ResourcesCompat.ID_NULL) {
            resources.getString(id, *formatArgs)
        } else {
            parent?.getString(originId, formatArgs)!!
        }
    }

    fun getString(@StringRes originId: Int, originName: String, originType: String, vararg formatArgs: Any): String {
        val id = getResourceIdentifier(originId, originName, originType)
        return if (id != ResourcesCompat.ID_NULL) {
            resources.getString(id, *formatArgs)
        } else {
            parent?.getString(originId, originName, originType, formatArgs)!!
        }
    }
}