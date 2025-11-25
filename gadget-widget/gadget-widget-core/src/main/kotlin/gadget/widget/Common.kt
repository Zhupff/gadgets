package gadget.widget

import android.app.Activity
import android.app.Dialog
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import java.util.concurrent.ConcurrentHashMap

object Common {

    private val VIEW_ID_CACHE = ConcurrentHashMap<String, Int>()

    @JvmStatic
    @IdRes
    fun getOrGenerateViewID(id: String): Int {
        return VIEW_ID_CACHE.getOrPut(id) { View.generateViewId() }
    }
}

inline val View.PARENT_ID: Int
    get() = (parent as? View)?.id ?: View.NO_ID

inline val String.VIEW_ID: Int
    get() = Common.getOrGenerateViewID(this)

inline fun <reified V : View> View.findViewById(
    id: String,
): V? = findViewById<V>(id.VIEW_ID)

inline fun <reified V : View> Activity.findViewById(
    id: String,
): V? = findViewById<V>(id.VIEW_ID)

inline fun <reified V : View> Fragment.findViewById(
    @IdRes id: Int,
): V? = view?.findViewById<V>(id)

inline fun <reified V : View> Fragment.findViewById(
    id: String,
): V? = view?.findViewById<V>(id.VIEW_ID)

inline fun <reified V : View> Dialog.findViewById(
    id: String,
): V? = findViewById<V>(id.VIEW_ID)