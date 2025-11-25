package gadget.widget.blur

import android.graphics.Bitmap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class BlurPixelsQueue(
    queueSize: Int = 3
) {
    companion object {
        const val STATE_IDLE = 0
        const val STATE_PREPARING = 1
        const val STATE_PREPARED = 2
        const val STATE_BLURRING = 3
        const val STATE_BLURRED = 4
        const val STATE_DRAWING = 5
        const val STATE_DRAWN = 6
    }

    private val queue = CopyOnWriteArrayList<Pixels>().also { queue ->
        for (i in 1..queueSize.coerceAtLeast(1)) {
            queue.add(Pixels(i))
        }
    }

    private var current = Pixels(0)

    fun acquireToPrepare(): Pixels? {
        queue.forEach { pixels ->
            if (pixels.state.compareAndSet(STATE_IDLE, STATE_PREPARING)) {
                queue.remove(pixels)
                return pixels
            }
        }
        queue.forEach { pixels ->
            if (pixels.state.compareAndSet(STATE_PREPARED, STATE_PREPARING)) {
                queue.remove(pixels)
                return pixels
            }
        }
        return null
    }

    fun acquireToBlur(): Pixels? {
        queue.forEach { pixels ->
            if (pixels.state.compareAndSet(STATE_PREPARED, STATE_BLURRING)) {
                queue.remove(pixels)
                return pixels
            }
        }
        return null
    }

    fun acquireToDraw(): Pixels? {
        queue.forEach { pixels ->
            if (pixels.state.compareAndSet(STATE_BLURRED, STATE_DRAWING)) {
                queue.remove(pixels)
                return pixels
            }
        }
        return null
    }


    inner class Pixels(
        val id: Int
    ) {
        internal val state = AtomicInteger(STATE_IDLE)
        var width: Int = 0; private set
        var height: Int = 0; private set
        var pixels: IntArray = IntArray(0); private set

        fun prepare(canvas: BlurCanvas, locations: IntArray) {
            if (locations[0] >= canvas.locations[2] ||
                locations[1] >= canvas.locations[3] ||
                locations[2] <= canvas.locations[0] ||
                locations[3] <= canvas.locations[1]) { // Outside
                width = 0
                height = 0
                pixels = IntArray(0)
                return
            }
            width = ((locations[2] - locations[0]) * canvas.scale).roundToInt()
            height = ((locations[3] - locations[1]) * canvas.scale).roundToInt()
            if (pixels.size != width * height) {
                pixels = IntArray(width * height)
            }
            val diffX = ((locations[0] - canvas.locations[0]) * canvas.scale).toInt()
            val diffY = ((locations[1] - canvas.locations[1]) * canvas.scale).toInt()
            canvas.bitmap.getPixels(
                pixels,
                (if (diffY >= 0) 0 else -diffY) * width + (if (diffX >= 0) 0 else -diffX),
                width,
                diffX.coerceAtLeast(0), diffY.coerceAtLeast(0),
                ((min(locations[2], canvas.locations[2]) - max(locations[0], canvas.locations[0])) * canvas.scale).roundToInt(),
                ((min(locations[3], canvas.locations[3]) - max(locations[1], canvas.locations[1])) * canvas.scale).roundToInt(),
            )
        }

        fun blur(blur: Blur, radius: Int) {
            if (radius > 0 && width > 0 && height > 0 && pixels.size == width * height) {
                blur.blur(pixels, width, height, radius)
            }
        }

        fun draw(bitmap: Bitmap?): Bitmap? {
            return if (width <= 0 || height <= 0 || pixels.size != width * height) {
                null
            } else if (bitmap == null || bitmap.isRecycled || bitmap.width != width || bitmap.height != height) {
                Bitmap.createBitmap(pixels, 0, width, width, height, bitmap?.config ?: Bitmap.Config.ARGB_8888)
            } else if (!bitmap.isMutable) {
                bitmap.copy(bitmap.config ?: Bitmap.Config.RGB_565, true).also {
                    it.setPixels(pixels, 0, width, 0, 0, width, height)
                }
            } else {
                bitmap.also {
                    it.setPixels(pixels, 0, width, 0, 0, width, height)
                }
            }
        }

        fun hasDrawn(): Boolean = this == current

        fun isFirstDraw(): Boolean = this.state.get() == STATE_DRAWING && current.state.get() == STATE_IDLE

        fun recycle() {
            if (this === current) {
                // do nothing
            } else if (state.compareAndSet(STATE_PREPARING, STATE_PREPARED) ||
                state.compareAndSet(STATE_BLURRING, STATE_BLURRED)) {
                queue.addIfAbsent(this)
            } else if (state.compareAndSet(STATE_DRAWING, STATE_DRAWN)) {
                val last = current
                current = this
                last.recycle()
            } else {
                state.set(STATE_IDLE)
                queue.addIfAbsent(this)
            }
        }

        override fun toString(): String = "BlurPixelsQueue(${hashCode()})-Pixels(${hashCode()}){state=${state.get()},width=$width,height=$height,size=${pixels.size}}"

        override fun hashCode(): Int = id

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Pixels) return false
            return width == other.width && height == other.height && pixels.contentEquals(other.pixels)
        }
    }
}