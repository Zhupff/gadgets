package zhupff.gadgets.widget.dsl

import android.view.View
import androidx.core.content.res.ResourcesCompat


private val VIEW_ID_CACHE: HashMap<String, Int> = HashMap()

val View.parentId: Int; get() = (parent as? View)?.id ?: View.NO_ID

val String.asViewId: Int; get() {
    if (VIEW_ID_CACHE.containsKey(this)) {
        val id = VIEW_ID_CACHE[this]
        if (id != null) {
            return id
        }
    }
    var id = RESOURCES.getIdentifier(this, "id", APPLICATION.packageName)
    if (id == ResourcesCompat.ID_NULL) {
        id = View.generateViewId()
    }
    VIEW_ID_CACHE[this] = id
    return id
}