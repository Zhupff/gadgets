package zhupff.gadgets.basic

import android.content.Context
import android.util.TypedValue
import android.view.View

fun Float.dp2px(context: Context = APPLICATION): Float = context.resources.displayMetrics.density * this

fun Float.px2dp(context: Context = APPLICATION): Float = this / context.resources.displayMetrics.density

fun Float.sp2px(context: Context = APPLICATION): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics)

fun Float.px2sp(context: Context = APPLICATION): Float = this / context.resources.displayMetrics.scaledDensity

fun Int.dp2px(context: Context = APPLICATION): Int = toFloat().dp2px(context).toInt()

fun Int.px2dp(context: Context = APPLICATION): Int = toFloat().px2dp(context).toInt()

fun Int.sp2px(context: Context = APPLICATION): Int = toFloat().sp2px(context).toInt()

fun Int.px2sp(context: Context = APPLICATION): Int = toFloat().px2sp(context).toInt()

fun Context.dp2px(dp: Float): Float = dp.dp2px(this)

fun Context.px2dp(px: Float): Float = px.px2dp(this)

fun Context.sp2px(sp: Float): Float = sp.sp2px(this)

fun Context.px2sp(px: Float): Float = px.px2sp(this)

fun Context.dp2px(dp: Int): Int = dp.dp2px(this)

fun Context.px2dp(px: Int): Int = px.px2dp(this)

fun Context.sp2px(sp: Int): Int = sp.sp2px(this)

fun Context.px2sp(px: Int): Int = px.px2sp(this)

fun View.dp2px(dp: Float): Float = dp.dp2px(context)

fun View.px2dp(px: Float): Float = px.px2dp(context)

fun View.sp2px(sp: Float): Float = sp.sp2px(context)

fun View.px2sp(px: Float): Float = px.px2sp(context)

fun View.dp2px(dp: Int): Int = dp.dp2px(context)

fun View.px2dp(px: Int): Int = px.px2dp(context)

fun View.sp2px(sp: Int): Int = sp.sp2px(context)

fun View.px2sp(px: Int): Int = px.px2sp(context)

val Float.dp: Float; get() = this.dp2px(APPLICATION)

val Float.sp: Float; get() = this.sp2px(APPLICATION)

val Int.dp: Int; get() = this.dp2px(APPLICATION)

val Int.sp: Int; get() = this.sp2px(APPLICATION)