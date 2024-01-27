package zhupf.gadget.theme

import android.view.View
import androidx.core.content.res.ResourcesCompat

abstract class ThemeAttribute(val attributeName: String) : Cloneable {

    companion object {
        const val TYPE_COLOR = "color"
        const val TYPE_DRAWABLE = "drawable"
        const val TYPE_STRING = "string"
    }

    var resourceId: Int = ResourcesCompat.ID_NULL; protected set

    var resourceName: String = ""; protected set

    var resourceType: String = ""; protected set

    abstract fun apply(view: View, theme: Theme)

    open fun copy(resourceId: Int, resourceName: String, resourceType: String): ThemeAttribute? =
        (clone() as? ThemeAttribute)?.also {
            it.resourceId = resourceId
            it.resourceName = resourceName
            it.resourceType = resourceType
        }

    override fun toString(): String = StringBuilder("{")
        .append("\"attributeName\":").append("\"$attributeName\",")
        .append("\"resourceId\":").append("$resourceId,")
        .append("\"resourceName\":").append("\"$resourceName\",")
        .append("\"resourceType\":").append("\"$resourceType\"")
        .append("}").toString()
}