package gadget.widget.blur

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.FrameLayout
import kotlin.math.roundToInt

open class BlurLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), OnPreDrawListener {

    companion object {
        var DEFAULT_BLUR_SCALE: Float = 0.25F
        var DEFAULT_BLUR_RADIUS: Int = 5

        const val BLUR_CANVAS_STRATEGY_INHERITED: Int = 0
        const val BLUR_CANVAS_STRATEGY_INDEPENDENT: Int = 1
        const val BLUR_DRAW_STRATEGY_IGNORE_IDENTICAL_FRAMES = 0
        const val BLUR_DRAW_STRATEGY_ALL_FRAMES = 1
    }

    var blurScale: Float = DEFAULT_BLUR_SCALE
        set(value) {
            assert(value in 0F..1F)
            if (field != value) {
                field = value
                if (isAttachedToWindow) {
                    postInvalidate()
                }
            }
        }

    var blurCanvasStrategy: Int = BLUR_CANVAS_STRATEGY_INHERITED
        set(value) {
            assert(
                value == BLUR_CANVAS_STRATEGY_INHERITED ||
                value == BLUR_CANVAS_STRATEGY_INDEPENDENT
            )
            if (field != value) {
                field = value
                if (isAttachedToWindow) {
                    postInvalidate()
                }
            }
        }

    protected var blurLayout: BlurLayout? = null

    protected var blurCanvas: BlurCanvas? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BlurLayout).also { typedArray ->
            blurScale = typedArray.getFloat(R.styleable.BlurLayout_gadget_blur_scale, DEFAULT_BLUR_SCALE)
            blurCanvasStrategy = typedArray.getInt(R.styleable.BlurLayout_gadget_blur_canvas, BLUR_CANVAS_STRATEGY_INHERITED)
        }.recycle()
    }

    override fun onPreDraw(): Boolean {
        if (blurCanvasStrategy == BLUR_CANVAS_STRATEGY_INDEPENDENT || blurLayout == null || blurLayout === this) {
            if (isShown && width > 0 && height > 0) {
                val targetWidth = (width * blurScale).roundToInt()
                val targetHeight = (height * blurScale).roundToInt()
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
                    getLocationInWindow(canvas.locations)
                    canvas.locations[2] = canvas.locations[0] + width
                    canvas.locations[3] = canvas.locations[1] + height
                    val c = canvas.save()
                    canvas.scale(blurScale, blurScale)
                    draw(canvas)
                    canvas.restoreToCount(c)
                }
            }
        } else {
            blurCanvas?.release()
            blurCanvas = null
        }
        return true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnPreDrawListener(this)
        blurLayout = findBlurLayoutAncestors(parent as? View)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        blurLayout = null
        viewTreeObserver.removeOnPreDrawListener(this)
        blurCanvas?.release()
        blurCanvas = null
    }

    protected fun findBlurLayoutAncestors(ancestor: View?): BlurLayout? {
        return when (ancestor) {
            null -> null
            is BlurLayout -> ancestor
            else -> findBlurLayoutAncestors(ancestor.parent as? View)
        }
    }
}