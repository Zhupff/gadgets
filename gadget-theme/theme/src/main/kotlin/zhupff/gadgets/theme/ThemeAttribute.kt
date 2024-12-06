package zhupff.gadgets.theme

import android.view.View
import androidx.core.content.res.ResourcesCompat

abstract class ThemeAttribute(
    val attributeName: String,
    resourceId: Int,
) : Cloneable {

    constructor(attributeName: String) : this(attributeName, ResourcesCompat.ID_NULL)

    companion object {
        const val TYPE_COLOR = "color"
        const val TYPE_DRAWABLE = "drawable"
        const val TYPE_STRING = "string"
    }

    var resourceId: Int = resourceId; protected set

    var resourceName: String = if (resourceId != ResourcesCompat.ID_NULL) {
        APPLICATION.resources.getResourceEntryName(resourceId)
    } else {
        ""
    }; protected set

    var resourceType: String = if (resourceId != ResourcesCompat.ID_NULL) {
        APPLICATION.resources.getResourceTypeName(resourceId)
    } else {
        ""
    }; protected set

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