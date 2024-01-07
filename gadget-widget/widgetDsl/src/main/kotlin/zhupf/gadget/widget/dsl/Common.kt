package zhupf.gadget.widget.dsl

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import zhupf.gadget.widget.WidgetDslScope
import zhupf.gadget.widget.common.ViewOnDoubleClickListener
import zhupf.gadget.widget.common.ViewOnMultiClickListener
import zhupf.gadget.widget.common.ViewOnSingleClickListener


// region ID

private val VIEW_ID_CACHE: HashMap<String, Int> = HashMap()

const val NO_ID: Int = View.NO_ID

val View.parentId: Int; get() = (parent as? View)?.id ?: View.NO_ID

val String.asViewId: Int; get() {
    if (VIEW_ID_CACHE.containsKey(this)) {
        val id = VIEW_ID_CACHE[this]
        if (id != null) {
            return id
        }
    }
    var id = DSL.resources.getIdentifier(this, "id", DSL.application.packageName)
    if (id == ResourcesCompat.ID_NULL) {
        id = generateViewId()
    }
    VIEW_ID_CACHE[this] = id
    return id
}

fun generateViewId(): Int = View.generateViewId()

inline fun <reified T : View> View.findViewById(
    id: String
): T? = findViewById(id.asViewId)

inline fun <reified T : View> Activity.findViewById(
    id: String
): T? = findViewById(id.asViewId)

inline fun <reified T : View> Fragment.findViewById(
    id: Int
): T? = view?.findViewById(id)

inline fun <reified T : View> Fragment.findViewById(
    id: String
): T? = view?.findViewById(id.asViewId)

// endregion

// region LayoutParams

const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParamsAs(
    block: (@WidgetDslScope T).() -> Unit
): T = (layoutParams as T).also {
    block(it)
    layoutParams = it
}

// endregion

// region initialize

fun <V : View> V.initialize(
    @IdRes id: Int = NO_ID,
    size: Pair<Int, Int>,
    parent: ViewGroup?,
) = initialize(id, ViewGroup.LayoutParams(size.first, size.second), parent)

fun <V : View> V.initialize(
    @IdRes id: Int = NO_ID,
    layoutParams: ViewGroup.LayoutParams,
    parent: ViewGroup?,
) = apply {
    if (id != NO_ID) {
        this.id = id
    }
    if (parent != null) {
        parent.addView(this, layoutParams)
    } else {
        this.layoutParams = layoutParams
    }
}

// endregion

// region listener

fun View.onClick(
    onClick: (View) -> Unit = {},
) {
    setOnClickListener(onClick)
}

fun View.onLongClick(
    onLongClick: (View) -> Boolean = { false },
) {
    setOnLongClickListener(onLongClick)
}

fun View.onSingleClick(
    interval: Long = 500L,
    onSingleClick: (View?) -> Unit,
) {
    setOnClickListener(object : ViewOnSingleClickListener(interval) {
        override fun onSingleClick(v: View?) {
            super.onSingleClick(v)
            onSingleClick(v)
        }
    })
}

fun View.onDoubleClick(
    interval1: Long = 500L,
    interval2: Long = 500L,
    onFirstClick: (View?) -> Unit = {},
    onSecondClick: (View?) -> Unit = {},
) {
    setOnClickListener(object : ViewOnDoubleClickListener(interval1, interval2) {
        override fun onFirstClick(v: View?) {
            super.onFirstClick(v)
            onFirstClick(v)
        }
        override fun onSecondClick(v: View?) {
            super.onSecondClick(v)
            onSecondClick(v)
        }
    })
}

fun View.onMultiClick(
    count: Int,
    onClick: (Int, View?) -> Unit,
) {
    setOnClickListener(object : ViewOnMultiClickListener(count) {
        override fun onClick(count: Int, v: View?) {
            super.onClick(count, v)
            onClick(count, v)
        }
    })
}

fun View.onMultiClick(
    intervals: LongArray,
    onClick: (Int, View?) -> Unit,
) {
    setOnClickListener(object : ViewOnMultiClickListener(intervals) {
        override fun onClick(count: Int, v: View?) {
            super.onClick(count, v)
            onClick(count, v)
        }
    })
}

// endregion