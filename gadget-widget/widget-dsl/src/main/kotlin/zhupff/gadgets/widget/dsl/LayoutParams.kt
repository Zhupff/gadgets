package zhupff.gadgets.widget.dsl

import android.view.View
import android.view.ViewGroup
import zhupff.gadgets.widget.DslScope

const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT

inline fun <reified L : ViewGroup.LayoutParams> View.layoutParamsAs(
    block: (@DslScope L).() -> Unit,
): L = (layoutParams as L).also {
    block(it)
    layoutParams = it
}

inline fun <reified L : ViewGroup.LayoutParams> View.layoutParamsAs(
    init: () -> L,
    block: (@DslScope L).() -> Unit,
): L = if (layoutParams != null) {
    layoutParams as L
} else {
    init()
}.also {
    block(it)
    layoutParams = it
}