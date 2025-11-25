package gadget.widget.blur

import android.graphics.Bitmap
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewTreeObserver.OnPreDrawListener
import kotlin.math.roundToInt

class BlurCanvasProvider : OnPreDrawListener, OnAttachStateChangeListener {

    var view: View? = null
        set(value) {
            if (field != value) {
                field?.let { oldValue ->
                    oldValue.viewTreeObserver.removeOnPreDrawListener(this)
                    oldValue.removeOnAttachStateChangeListener(this)
                }
                field = value
                field?.let { newValue ->
                    if (newValue.isAttachedToWindow) {
                        newValue.viewTreeObserver.addOnPreDrawListener(this)
                    }
                    newValue.addOnAttachStateChangeListener(this)
                }
            }
        }

    var blurScale: Float = BlurLayout.DEFAULT_BLUR_SCALE
        set(value) {
            assert(value in 0F..1F)
            if (field != value) {
                field = value
                if (view?.isAttachedToWindow == true) {
                    view?.postInvalidate()
                }
            }
        }

    private var blurCanvas: BlurCanvas? = null

    override fun onPreDraw(): Boolean {
        val v = view
        if (v != null && v.isShown && v.width > 0 && v.height > 0) {
            val targetWidth = (v.width * blurScale).roundToInt()
            val targetHeight = (v.height * blurScale).roundToInt()
            var canvas = blurCanvas
            var bitmap = canvas?.bitmap
            if (canvas == null || !canvas.isValid() ||
                bitmap == null || bitmap.isRecycled ||
                bitmap.width != targetWidth || bitmap.height != targetHeight) {
                canvas?.release()
                blurCanvas = BlurCanvas(Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888), blurScale)
            }
            canvas = blurCanvas
            bitmap = canvas?.bitmap
            if (canvas != null && bitmap != null) {
                v.getLocationInWindow(canvas.locations)
                canvas.locations[2] = canvas.locations[0] + v.width
                canvas.locations[3] = canvas.locations[1] + v.height
                val c = canvas.save()
                canvas.scale(blurScale, blurScale)
                v.draw(canvas)
                canvas.restoreToCount(c)
            }
        } else {
            blurCanvas?.release()
            blurCanvas = null
        }
        return true
    }

    override fun onViewAttachedToWindow(v: View) {
        view?.viewTreeObserver?.addOnPreDrawListener(this)
    }

    override fun onViewDetachedFromWindow(v: View) {
        view?.viewTreeObserver?.removeOnPreDrawListener(this)
        blurCanvas?.release()
        blurCanvas = null
    }
}