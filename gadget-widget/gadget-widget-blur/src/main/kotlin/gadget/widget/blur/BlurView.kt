package gadget.widget.blur

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import gadget.widget.blur.BlurLayout.Companion.BLUR_DRAW_STRATEGY_ALL_FRAMES
import gadget.widget.blur.BlurLayout.Companion.BLUR_DRAW_STRATEGY_IGNORE_IDENTICAL_FRAMES

class BlurView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var blurRadius: Int = BlurLayout.DEFAULT_BLUR_RADIUS
        set(value) {
            assert(value >= 0)
            if (field != value) {
                field = value
                if (isAttachedToWindow) {
                    postInvalidate()
                }
            }
        }

    var blurDrawStrategy: Int = BLUR_DRAW_STRATEGY_IGNORE_IDENTICAL_FRAMES
        set(value) {
            assert(
                value == BLUR_DRAW_STRATEGY_IGNORE_IDENTICAL_FRAMES ||
                value == BLUR_DRAW_STRATEGY_ALL_FRAMES
            )
            if (field != value) {
                field = value
                if (isAttachedToWindow) {
                    postInvalidate()
                }
            }
        }

    var blur: Blur = BlurCompat()

    private val blurPixelsQueue = BlurPixelsQueue(1)

    private val locations = IntArray(4)

    private val srcRect = Rect()

    private val dstRect = Rect()

    private var drawBitmap: Bitmap? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BlurView).also { typedArray ->
            blurRadius = typedArray.getInt(R.styleable.BlurView_gadget_blur_radius, blurRadius)
            blurDrawStrategy = typedArray.getInt(R.styleable.BlurView_gadget_blur_draw, blurDrawStrategy)
        }.recycle()
    }

    override fun draw(canvas: Canvas) {
        if (canvas is BlurCanvas) {
            if (isShown && !dstRect.isEmpty) {
                var pixels = blurPixelsQueue.acquireToPrepare()!!
                pixels.prepare(canvas, locations)
                pixels.recycle()
                pixels = blurPixelsQueue.acquireToBlur()!!
                pixels.blur(blur, blurRadius)
                pixels.recycle()
                pixels = blurPixelsQueue.acquireToDraw()!!
                if (blurDrawStrategy == BLUR_DRAW_STRATEGY_ALL_FRAMES || !pixels.hasDrawn()) {
                    drawBitmap = pixels.draw(drawBitmap)
                    postInvalidate()
                }
                pixels.recycle()
            }
        }
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        try {
            drawBitmap?.let { bm ->
                srcRect.set(0, 0, bm.width, bm.height)
                canvas.drawBitmap(bm, srcRect, dstRect, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("Range")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            getLocationInWindow(locations)
            locations[2] = locations[0] + (right - left)
            locations[3] = locations[1] + (bottom - top)
            dstRect.set(0, 0, right - left, bottom - top)
        }
    }
}