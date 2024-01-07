package zhupf.gadget.basic

import android.content.Context
import android.util.TypedValue
import android.view.View

fun dp2px(dp: Float, context: Context = Basic.application): Float = context.resources.displayMetrics.density * dp

fun px2dp(px: Float, context: Context = Basic.application): Float = px / context.resources.displayMetrics.density

fun sp2px(sp: Float, context: Context = Basic.application): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)

fun px2sp(px: Float, context: Context = Basic.application): Float = px / context.resources.displayMetrics.scaledDensity

fun Context.dp2px(dp: Float): Float = dp2px(dp, this)

fun Context.px2dp(px: Float): Float = px2dp(px, this)

fun Context.sp2px(sp: Float): Float = sp2px(sp, this)

fun Context.px2sp(px: Float): Float = px2sp(px, this)

fun View.dp2px(dp: Float): Float = dp2px(dp, context)

fun View.px2dp(px: Float): Float = px2dp(px, context)

fun View.sp2px(sp: Float): Float = sp2px(sp, context)

fun View.px2sp(px: Float): Float = px2sp(px, context)

fun Int.dp2px(context: Context = Basic.application): Float = dp2px(this.toFloat(), context)

fun Int.px2dp(context: Context = Basic.application): Float = px2dp(this.toFloat(), context)

fun Int.sp2px(context: Context = Basic.application): Float = sp2px(this.toFloat(), context)

fun Int.px2sp(context: Context = Basic.application): Float = px2sp(this.toFloat(), context)

fun Float.dp2px(context: Context = Basic.application): Float = dp2px(this, context)

fun Float.px2dp(context: Context = Basic.application): Float = px2dp(this, context)

fun Float.sp2px(context: Context = Basic.application): Float = sp2px(this, context)

fun Float.px2sp(context: Context = Basic.application): Float = px2sp(this, context)