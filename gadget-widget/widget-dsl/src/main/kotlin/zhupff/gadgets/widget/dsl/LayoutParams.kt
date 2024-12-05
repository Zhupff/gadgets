package zhupff.gadgets.widget.dsl

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
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

// region ConstraintLayout.LayoutParams

internal typealias ConstraintLayoutParams = ConstraintLayout.LayoutParams

internal val ConstraintLayoutParams.UNSET: Int; get() = ConstraintLayoutParams.UNSET
internal val ConstraintLayoutParams.PARENT_ID: Int; get() = ConstraintLayoutParams.PARENT_ID

fun ConstraintLayoutParams.unsetHorizontal() {
    startToStart = UNSET
    startToEnd = UNSET
    endToEnd = UNSET
    endToStart = UNSET
}

fun ConstraintLayoutParams.unsetVertical() {
    topToTop = UNSET
    topToBottom = UNSET
    bottomToBottom = UNSET
    bottomToTop = UNSET
}

fun ConstraintLayoutParams.unset() {
    unsetHorizontal()
    unsetVertical()
}

fun ConstraintLayoutParams.centerHorizontal() {
    startToStart = PARENT_ID
    endToEnd = PARENT_ID
}

fun ConstraintLayoutParams.centerVertical() {
    topToTop = PARENT_ID
    bottomToBottom = PARENT_ID
}

fun ConstraintLayoutParams.center() {
    centerHorizontal()
    centerVertical()
}

// endregion