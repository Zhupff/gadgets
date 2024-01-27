package zhupf.gadget.blur

import android.graphics.Bitmap
import android.os.SystemClock
import kotlin.math.sqrt

class BlurConfig {
    companion object {
        const val DEFAULT_RADIUS = 5
        const val DEFAULT_SCALE = 1F
        const val DEFAULT_MAX_AREA = 16 * 9 * 20 * 20
        const val DEFAULT_TIMESTAMP = 0L
    }

    var bitmap: Bitmap? = null
        private set

    var radius: Int = DEFAULT_RADIUS
        private set

    var scaleX: Float = DEFAULT_SCALE
        private set

    var scaleY: Float = DEFAULT_SCALE
        private set

    var maxArea: Int = DEFAULT_MAX_AREA
        private set

    var timestamp: Long = DEFAULT_TIMESTAMP
        private set

    fun bitmap(origin: Bitmap, copy: Boolean = true) = apply {
        this.bitmap = if (copy) origin.copy(origin.config, true) else origin
    }

    fun radius(r: Int) = apply {
        this.radius = r
    }

    fun scale(x: Float, y: Float) = apply {
        this.maxArea = -1
        this.scaleX = x
        this.scaleY = y
    }

    fun scale(max: Int) = apply {
        this.scaleX = DEFAULT_SCALE
        this.scaleY = DEFAULT_SCALE
        this.maxArea = max
    }

    fun reset() = apply {
        this.bitmap = null
        this.radius = DEFAULT_RADIUS
        this.scaleX = DEFAULT_SCALE
        this.scaleY = DEFAULT_SCALE
        this.maxArea = DEFAULT_MAX_AREA
        this.timestamp = DEFAULT_TIMESTAMP
    }

    fun config() {
        timestamp = SystemClock.elapsedRealtime()
        val bm = bitmap ?: return
        if (maxArea > 0) {
            if (bm.width * bm.height > maxArea) {
                val scale = sqrt(maxArea.toFloat() / (bm.width * bm.height).toFloat())
                bitmap = Bitmap.createScaledBitmap(bm, (scale * bm.width).toInt(), (scale * bm.height).toInt(), false)
            }
        } else if (scaleX != DEFAULT_SCALE || scaleY != DEFAULT_SCALE) {
            bitmap = Bitmap.createScaledBitmap(bm, (scaleX * bm.width).toInt(), (scaleY * bm.height).toInt(), false)
        }
    }
}