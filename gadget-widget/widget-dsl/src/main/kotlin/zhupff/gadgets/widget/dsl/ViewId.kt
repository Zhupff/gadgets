package zhupff.gadgets.widget.dsl

import android.app.Activity
import android.app.Dialog
import android.view.View
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment


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

inline fun <reified V : View> View.findViewById(
    id: String,
): V? = findViewById<V>(id.asViewId)

inline fun <reified V : View> Activity.findViewById(
    id: String,
): V? = findViewById<V>(id.asViewId)

inline fun <reified V : View> Fragment.findViewById(
    @IdRes id: Int,
): V? = view?.findViewById<V>(id)

inline fun <reified V : View> Fragment.findViewById(
    id: String,
): V? = view?.findViewById<V>(id.asViewId)

inline fun <reified V : View> Dialog.findViewById(
    id: String,
): V? = findViewById<V>(id.asViewId)