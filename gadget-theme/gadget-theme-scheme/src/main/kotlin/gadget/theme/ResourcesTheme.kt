package gadget.theme

import android.content.Context
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import java.io.File

open class ResourcesTheme(
    id: String,
    parent: Theme?,
    val packageName: String,
    val resources: Resources,
) : Theme(id, parent) {

    companion object {

        @JvmStatic
        fun load1(
            context: Context,
            apk: File,
            id: String,
            parent: Theme? = null,
        ): ResourcesTheme? = try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(apk.absolutePath, 0)
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPath = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assetManager, apk.absolutePath)
            val resources = Resources(assetManager, context.resources.displayMetrics, context.resources.configuration)
            ResourcesTheme(id, parent, packageInfo.packageName, resources)
        } catch (throwable: Throwable) {
            null
        }

        @JvmStatic
        fun load2(
            context: Context,
            apk: File,
            id: String,
            parent: Theme? = null,
        ): ResourcesTheme? = try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(apk.absolutePath, 0)
            val applicationInfo = packageInfo.applicationInfo!!
            applicationInfo.sourceDir = apk.absolutePath
            applicationInfo.publicSourceDir = apk.absolutePath
            val resources = packageManager.getResourcesForApplication(applicationInfo)
            ResourcesTheme(id, parent, packageInfo.packageName, resources)
        } catch (throwable: Throwable) {
            null
        }
    }

    protected val idCaches = HashMap<Int, Int>()

    open fun getIdentifier(r: Resource): Int {
        if (parent == null) {
            return r.id
        }
        return idCaches.getOrPut(r.id) {
            try {
                resources.getIdentifier(r.name, r.type, packageName)
            } catch (throwable: Throwable) {
                ResourcesCompat.ID_NULL
            }
        }
    }

    override fun getColorInt(r: Resource): Int {
        val id = getIdentifier(r)
        if (id != ResourcesCompat.ID_NULL) {
            return ResourcesCompat.getColor(resources, id, null)
        }
        return parent?.getColorInt(r) ?: throw IllegalStateException("$r not found!")
    }

    override fun getColorState(r: Resource): ColorStateList? {
        val id = getIdentifier(r)
        if (id != ResourcesCompat.ID_NULL) {
            return ResourcesCompat.getColorStateList(resources, id, null)
        }
        return parent?.getColorState(r) ?: throw IllegalStateException("$r not found!")
    }

    override fun getDrawable(r: Resource): Drawable? {
        val id = getIdentifier(r)
        if (id != ResourcesCompat.ID_NULL) {
            return ResourcesCompat.getDrawable(resources, id, null)
        }
        return parent?.getDrawable(r) ?: throw IllegalStateException("$r not found!")
    }

    override fun getString(r: Resource, vararg args: Any): String? {
        val id = getIdentifier(r)
        if (id != ResourcesCompat.ID_NULL) {
            return resources.getString(id, *args)
        }
        return parent?.getString(r, *args) ?: throw IllegalStateException("$r not found!")
    }
}