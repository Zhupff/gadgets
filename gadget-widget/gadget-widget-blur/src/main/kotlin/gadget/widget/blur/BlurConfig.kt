package gadget.widget.blur

import android.graphics.Bitmap
import kotlin.math.roundToInt
import kotlin.math.sqrt

class BlurConfig {
    companion object {
        const val DEFAULT_RADIUS = 5
        const val DEFAULT_SCALE = 0.5F
        const val DEFAULT_MAX_AREA = 16 * 9 * 20 * 20
    }

    private var radius: Int = DEFAULT_RADIUS

    private var scale: Float = DEFAULT_SCALE

    private var maxArea: Int = DEFAULT_MAX_AREA

    fun radius(radius: Int) = apply {
        this.radius = radius
    }

    fun scale(scale: Float) = apply {
        assert(scale > 0F)
        this.scale = scale
        this.maxArea = 0
    }

    fun maxArea(maxArea: Int) = apply {
        assert(maxArea > 0)
        this.maxArea = maxArea
        this.scale = 1F
    }

    fun blur(blur: Blur, target: Bitmap, copy: Boolean = true): Bitmap {
        var width: Int = target.width
        var height: Int = target.height
        if (maxArea > 0) {
            if (width * height > maxArea) {
                val sc = sqrt(maxArea.toFloat() / (width * height).toFloat())
                width = (width * sc).roundToInt()
                height = (height * sc).roundToInt()
            }
        } else if (scale != 1F) {
            width = (width * scale).roundToInt()
            height = (height * scale).roundToInt()
        }
        var result = target
        if (copy || !target.isMutable || width != target.width || height != target.height) {
            result = Bitmap.createScaledBitmap(target, width, height, false)
        }
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)
        blur.blur(pixels, width, height, radius)
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    override fun toString(): String = "BlurConfig(${hashCode()}{radius=$radius,scale=$scale,maxArea=$maxArea})"
}