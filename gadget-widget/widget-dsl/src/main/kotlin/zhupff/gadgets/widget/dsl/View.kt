package zhupff.gadgets.widget.dsl

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat

fun <V : View> V.initialize(
    @IdRes id: Int = View.NO_ID,
    size: Pair<Int, Int> = WRAP_CONTENT to WRAP_CONTENT,
    parent: ViewGroup? = null,
    index: Int = -1
): V = initialize(id, ViewGroup.LayoutParams(size.first, size.second), parent, index)

fun <V : View> V.initialize(
    @IdRes id: Int = View.NO_ID,
    layoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT),
    parent: ViewGroup? = null,
    index: Int = -1,
): V = apply {
    if (id != View.NO_ID && id != ResourcesCompat.ID_NULL) {
        this.id = id
    }
    if (parent != null) {
        parent.addView(this, index, layoutParams)
    } else {
        this.layoutParams = layoutParams
    }
}