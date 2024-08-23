package zhupff.gadgets.widget.cornerclip

import android.graphics.Outline
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import zhupff.gadgets.widget.R
import kotlin.math.min
import kotlin.math.roundToInt

class CornerClip @JvmOverloads constructor(
    val target: View,
    /**
     * 如果小于零，则使用较短边的一半长度作为半径.
     */
    radius: Float = -1F,
) : ViewOutlineProvider() {

    companion object {

        @JvmStatic
        fun get(target: View): CornerClip? = target.getTag(R.id.gadgets_view_corner_clip) as? CornerClip

        @JvmStatic
        fun reset(target: View) {
            target.setTag(R.id.gadgets_view_corner_clip, null)
            target.outlineProvider = BACKGROUND
        }
    }

    init {
        target.setTag(R.id.gadgets_view_corner_clip, this)
        target.clipToOutline = true
        target.outlineProvider = this
    }

    /**
     * if value < 0: radius = shorter-side / 2
     * if value >= 0: radius = value
     * see [getOutline]
     */
    var radius: Float = radius
        set(value) {
            if (get(target) === this) {
                if (field != value) {
                    field = value
                    target.invalidateOutline()
                }
            }
        }

    /**
     * see [getOutline]
     */
    var gravity: Int = Gravity.LEFT or Gravity.TOP or Gravity.RIGHT or Gravity.BOTTOM
        set(value) {
            if (get(target) === this)
            if (field != value) {
                field = value
                target.invalidateOutline()
            }
        }

    override fun getOutline(view: View?, outline: Outline?) {
        if (view === target && outline != null) {
            val targetRadius = if (this.radius < 0F) min(view.width, view.height) / 2F else this.radius
            when (this.gravity) {
                /**
                 *  / * * \
                 *  *     *
                 *  \ * * /
                 */
                Gravity.LEFT or Gravity.TOP or Gravity.RIGHT or Gravity.BOTTOM -> {
                    outline.setRoundRect(0, 0, view.width, view.height, targetRadius)
                }
                /**
                 *  / * * *
                 *  *     *
                 *  * * * *
                 */
                Gravity.LEFT or Gravity.TOP -> {
                    outline.setRoundRect(0, 0, view.width + targetRadius.roundToInt(), view.height + targetRadius.roundToInt(), targetRadius)
                }
                /**
                 *  * * * \
                 *  *     *
                 *  * * * *
                 */
                Gravity.RIGHT or Gravity.TOP -> {
                    outline.setRoundRect(0 - targetRadius.roundToInt(), 0, view.width, view.height + targetRadius.roundToInt(), targetRadius)
                }
                /**
                 *  * * * *
                 *  *     *
                 *  * * * /
                 */
                Gravity.RIGHT or Gravity.BOTTOM -> {
                    outline.setRoundRect(0 - targetRadius.roundToInt(), 0 - targetRadius.roundToInt(), view.width, view.height, targetRadius)
                }
                /**
                 *  * * * *
                 *  *     *
                 *  \ * * *
                 */
                Gravity.LEFT or Gravity.BOTTOM -> {
                    outline.setRoundRect(0, 0 - targetRadius.roundToInt(), view.width + targetRadius.roundToInt(), view.height, targetRadius)
                }
                /**
                 *  / * * *
                 *  *     *
                 *  \ * * *
                 */
                Gravity.LEFT -> {
                    outline.setRoundRect(0, 0, view.width + targetRadius.roundToInt(), view.height, targetRadius)
                }
                /**
                 *  / * * \
                 *  *     *
                 *  * * * *
                 */
                Gravity.TOP -> {
                    outline.setRoundRect(0, 0, view.width, view.height + targetRadius.roundToInt(), targetRadius)
                }
                /**
                 *  * * * \
                 *  *     *
                 *  * * * /
                 */
                Gravity.RIGHT -> {
                    outline.setRoundRect(0 - targetRadius.roundToInt(), 0, view.width, view.height, targetRadius)
                }
                /**
                 *  * * * *
                 *  *     *
                 *  \ * * /
                 */
                Gravity.BOTTOM -> {
                    outline.setRoundRect(0, 0 - targetRadius.roundToInt(), view.width, view.height, targetRadius)
                }
                else -> {
                    throw IllegalArgumentException("NOT support gravity(${this.gravity})")
                }
            }
        }
    }
}


var View.cornerClip: CornerClip?
    get() = CornerClip.get(this)
    set(value) {
        if (value == null) {
            CornerClip.reset(this)
        } else {
            if (value !== CornerClip.get(this)) {
                throw IllegalArgumentException("Use CornerClip constructor instead.")
            }
        }
    }