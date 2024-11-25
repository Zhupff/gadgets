package zhupff.gadgets.theme

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.annotation.MainThread
import androidx.core.content.res.ResourcesCompat
import org.json.JSONObject

open class ResourceTheme @MainThread constructor(
    name: String,
    parent: Theme?,
    val resources: Resources,
) : Theme(name, parent) {
    companion object {
        const val INFO_THEME_ID = "theme_id"

        @JvmStatic
        fun loadResources(filePath: String): Resources? {
            return try {
                val assetManager = AssetManager::class.java.newInstance()
                val addAssetPath = assetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
                addAssetPath.invoke(assetManager, filePath)
                Resources(assetManager, APPLICATION.resources.displayMetrics, APPLICATION.resources.configuration)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    val info: JSONObject = try {
        JSONObject(resources.assets.open("theme.json").reader(Charsets.UTF_8).readText())
    } catch (e: Exception) {
        e.printStackTrace()
        throw IllegalArgumentException("theme.json not found!")
    }

    val themeId: String = try {
        if (isOrigin) APPLICATION.packageName else info.getString(INFO_THEME_ID)
    } catch (e: Exception) {
        throw IllegalArgumentException("Is this an original theme? Otherwise it should have a theme_id which is its package name.")
    }

    protected val idCache = HashMap<Int, Int>()

    open fun getIdentifier(@AnyRes id: Int): Int {
        return idCache.getOrPut(id) {
            try {
                val name = APPLICATION.resources.getResourceEntryName(id)
                val type = APPLICATION.resources.getResourceTypeName(id)
                resources.getIdentifier(name, type, themeId)
            } catch (throwable: Throwable) {
                ResourcesCompat.ID_NULL
            }
        }
    }

    override fun getColor(id: Int): Any? {
        val id2 = getIdentifier(id)
        if (id2 == ResourcesCompat.ID_NULL) {
            return parent?.getColor(id)
        }
        val colorStateList = try {
            ResourcesCompat.getColorStateList(resources, id2, null)
        } catch (throwable: Throwable) {
            null
        }
        if (colorStateList != null) {
            return colorStateList
        }
        val color = try {
            ResourcesCompat.getColor(resources, id2, null)
        } catch (throwable: Throwable) {
            null
        }
        if (color != null) {
            return color
        }
        return null
    }

    override fun getDrawable(id: Int): Drawable? {
        val id2 = getIdentifier(id)
        if (id2 == ResourcesCompat.ID_NULL) {
            return parent?.getDrawable(id)
        }
        val drawable = try {
            ResourcesCompat.getDrawable(resources, id2, null)
        } catch (throwable: Throwable) {
            null
        }
        if (drawable != null) {
            return drawable
        }
        return null
    }

    override fun getString(id: Int, vararg args: Any): String? {
        val id2 = getIdentifier(id)
        if (id2 == ResourcesCompat.ID_NULL) {
            return parent?.getString(id, *args)
        }
        val string = try {
            if (args.isEmpty()) {
                resources.getString(id2)
            } else {
                resources.getString(id, *args)
            }
        } catch (throwable: Throwable) {
            null
        }
        if (string != null) {
            return string
        }
        return null
    }
}