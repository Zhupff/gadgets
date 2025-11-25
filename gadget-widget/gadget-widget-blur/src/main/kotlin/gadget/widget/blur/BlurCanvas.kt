package gadget.widget.blur

import android.graphics.Bitmap
import android.graphics.Canvas

class BlurCanvas(
    val bitmap: Bitmap,
    val scale: Float,
    /** (left, top, right, bottom) in window. */
    val locations: IntArray = IntArray(4)
) : Canvas(bitmap) {

    fun isValid(): Boolean = !bitmap.isRecycled && bitmap.width > 0 && bitmap.height > 0 && scale > 0F

    fun release() {
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
    }
}