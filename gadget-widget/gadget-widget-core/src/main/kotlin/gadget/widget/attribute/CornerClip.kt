package gadget.widget.attribute

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.MainThread
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 圆角裁切，支持单个角、单个边和全角裁切。
 */
class CornerClip<V : View> @JvmOverloads constructor(
    private val view: V,
    radius: Float = 0F,
    gravity: Int = N,
) : ViewOutlineProvider() {

    companion object {
        const val N  = 0
        const val L  = 1
        const val T  = 2
        const val R  = 3
        const val B  = 4
        const val TL = 5
        const val TR = 6
        const val BR = 7
        const val BL = 8
        const val A  = 9
    }

    /**
     * 当该值小于0时，使用短边的一半作为半径。
     */
    var radius: Float = if (radius < 0F) -1F else radius
        @MainThread
        set(value) {
            val newValue = if (value < 0F) -1F else value
            if (field != newValue) {
                field = newValue
                this.view.invalidateOutline()
            }
        }
    private val radiusArray = FloatArray(8)

    var gravity: Int = if (gravity in N..A) gravity else N
        @MainThread
        set(value) {
            val newValue = if (value in N..A) value else throw IllegalArgumentException("Not support $value!")
            if (field != newValue) {
                field = newValue
                this.view.invalidateOutline()
            }
        }

    @Volatile var once: Boolean = false
        private set

    init {
        this.view.clipToOutline = true
        this.view.outlineProvider = this
        this.once = true
    }

    override fun getOutline(view: View?, outline: Outline?) {
        if (this.view !== view) {
            throw IllegalStateException("Not the same view!")
        }
        if (outline == null) {
            return
        }
        val r = if (radius < 0F) min(view.width, view.height) / 2F else radius
        when (gravity) {
            N -> {
                outline.setRect(0, 0, view.width, view.height)
            }
            /**
             *  / * * *
             *  *     *
             *  \ * * *
             */
            L -> {
                outline.setRoundRect(0, 0, view.width + r.roundToInt(), view.height, r)
            }
            /**
             *  / * * \
             *  *     *
             *  * * * *
             */
            T -> {
                outline.setRoundRect(0, 0, view.width, view.height + r.roundToInt(), r)
            }
            /**
             *  * * * \
             *  *     *
             *  * * * /
             */
            R -> {
                outline.setRoundRect(0 - r.roundToInt(), 0, view.width, view.height, r)
            }
            /**
             *  * * * *
             *  *     *
             *  \ * * /
             */
            B -> {
                outline.setRoundRect(0, 0 - r.roundToInt(), view.width, view.height, r)
            }
            /**
             *  / * * *
             *  *     *
             *  * * * *
             */
            TL -> {
                outline.setRoundRect(0, 0, view.width + r.roundToInt(), view.height + r.roundToInt(), r)
            }
            /**
             *  * * * \
             *  *     *
             *  * * * *
             */
            TR -> {
                outline.setRoundRect(0 - r.roundToInt(), 0, view.width, view.height + r.roundToInt(), r)
            }
            /**
             *  * * * *
             *  *     *
             *  * * * /
             */
            BR -> {
                outline.setRoundRect(0 - r.roundToInt(), 0 - r.roundToInt(), view.width, view.height, r)
            }
            /**
             *  * * * *
             *  *     *
             *  \ * * *
             */
            BL -> {
                outline.setRoundRect(0, 0 - r.roundToInt(), view.width + r.roundToInt(), view.height, r)
            }
            /**
             *  / * * \
             *  *     *
             *  \ * * /
             */
            A -> {
                outline.setRoundRect(0, 0, view.width, view.height, r)
            }
        }
    }

    fun getRadiusArray(): FloatArray {
        radiusArray[0] = 0F; radiusArray[1] = 0F; radiusArray[2] = 0F; radiusArray[3] = 0F
        radiusArray[4] = 0F; radiusArray[5] = 0F; radiusArray[6] = 0F; radiusArray[7] = 0F
        var r = min(view.width, view.height) / 2F
        r = if (radius < 0F) r else min(r, radius)
        when (gravity) {
            L -> {
                radiusArray[0] = r; radiusArray[1] = r
                radiusArray[6] = r; radiusArray[7] = r
            }
            T -> {
                radiusArray[0] = r; radiusArray[1] = r
                radiusArray[2] = r; radiusArray[3] = r
            }
            R -> {
                radiusArray[2] = r; radiusArray[3] = r
                radiusArray[4] = r; radiusArray[5] = r
            }
            B -> {
                radiusArray[4] = r; radiusArray[5] = r
                radiusArray[6] = r; radiusArray[7] = r
            }
            TL -> {
                radiusArray[0] = r; radiusArray[1] = r
            }
            TR -> {
                radiusArray[2] = r; radiusArray[3] = r
            }
            BR -> {
                radiusArray[4] = r; radiusArray[5] = r
            }
            BL -> {
                radiusArray[6] = r; radiusArray[7] = r
            }
            A -> {
                radiusArray[0] = r; radiusArray[1] = r
                radiusArray[2] = r; radiusArray[3] = r
                radiusArray[4] = r; radiusArray[5] = r
                radiusArray[6] = r; radiusArray[7] = r
            }
        }
        return radiusArray
    }
}