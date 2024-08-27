package zhupff.gadgets.widget.dsl

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat

private val DEFAULT_W = MATCH_PARENT
private val DEFAULT_H = MATCH_PARENT
private val DEFAULT_LP: ViewGroup.LayoutParams; get() = ViewGroup.LayoutParams(DEFAULT_W, DEFAULT_H)
private val DEFAULT_WH: Pair<Int, Int>; get() = DEFAULT_W to DEFAULT_H

fun <V : View> V.initialize(
    @IdRes id: Int = View.NO_ID,
    layoutParams: ViewGroup.LayoutParams = DEFAULT_LP,
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

fun <V : View> V.initialize(
    @IdRes id: Int = View.NO_ID,
    size: Pair<Int, Int> = DEFAULT_WH,
    parent: ViewGroup? = null,
    index: Int = -1,
): V = initialize(id, ViewGroup.LayoutParams(size.first, size.second), parent, index)

fun <V : View> V.initialize(
    id: String,
    layoutParams: ViewGroup.LayoutParams = DEFAULT_LP,
    parent: ViewGroup? = null,
    index: Int = -1,
): V = initialize(id.asViewId, layoutParams, parent, index)

fun <V : View> V.initialize(
    id: String,
    size: Pair<Int, Int> = DEFAULT_WH,
    parent: ViewGroup? = null,
    index: Int = -1,
): V = initialize(id.asViewId, size, parent, index)