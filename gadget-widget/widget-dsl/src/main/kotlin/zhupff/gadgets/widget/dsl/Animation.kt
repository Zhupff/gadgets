package zhupff.gadgets.widget.dsl

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator

inline fun rotationAnimator(
    target: View,
    vararg values: Float,
    block: (ObjectAnimator).() -> Unit = {},
): ObjectAnimator {
    return ObjectAnimator.ofFloat(target, "rotation", *values).also(block)
}

fun ValueAnimator.infinite() {
    this.repeatCount = ValueAnimator.INFINITE
}

fun ValueAnimator.linearInterpolator(
    block: (LinearInterpolator).() -> Unit = {},
) {
    this.interpolator = LinearInterpolator().also(block)
}