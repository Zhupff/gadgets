package gadget.basic

import android.content.res.Resources
import android.util.TypedValue

fun Number.dp(resources: Resources = applicationContext.resources): Float = resources.displayMetrics.density * this.toFloat()

fun Number.sp(resources: Resources = applicationContext.resources): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), resources.displayMetrics)

val Number.dp: Float
    get() = this.dp()

val Number.sp: Float
    get() = this.sp()