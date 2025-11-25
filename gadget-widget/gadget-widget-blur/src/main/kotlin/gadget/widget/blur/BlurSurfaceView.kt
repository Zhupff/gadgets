package gadget.widget.blur

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class BlurSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    val surfaceView = SurfaceView(context)

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

    var blur: Blur = BlurCompat()

    private val blurPixelsQueue = BlurPixelsQueue(3)

    private val locations = IntArray(4)

    private val srcRect = Rect()

    private val dstRect = Rect()

    private var drawBitmap: Bitmap? = null

    private var executor: ExecutorService? = null

    private val isBlurring = AtomicBoolean(false)

    private val blurRunnable = Runnable {
        while (isBlurring.get()) {
            val pixels: BlurPixelsQueue.Pixels?
            synchronized(isBlurring) {
                pixels = blurPixelsQueue.acquireToBlur()
                if (pixels == null) {
                    isBlurring.set(false)
                }
            }
            if (pixels != null) {
                pixels.blur(blur, blurRadius)
                pixels.recycle()
                if (isDrawing.compareAndSet(false, true)) {
                    executor?.execute(drawRunnable)
                }
            }
        }
    }

    private val isDrawing = AtomicBoolean(false)

    private val drawRunnable = Runnable {
        while (isDrawing.get()) {
            val pixels: BlurPixelsQueue.Pixels?
            synchronized(isDrawing) {
                pixels = blurPixelsQueue.acquireToDraw()
                if (pixels == null) {
                    isDrawing.set(false)
                }
            }
            if (pixels != null) {
                drawBitmap = pixels.draw(drawBitmap)?.also { bm ->
                    srcRect.set(0, 0, bm.width, bm.height)
                    val canvas = surfaceView.holder.lockCanvas()
                    if (canvas != null) {
                        canvas.drawBitmap(bm, srcRect, dstRect, null)
                        surfaceView.holder.unlockCanvasAndPost(canvas)
                    }
                }
                pixels.recycle()
            }
        }
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BlurSurfaceView).also { typedArray ->
            blurRadius = typedArray.getInt(R.styleable.BlurSurfaceView_gadget_blur_radius, blurRadius)
        }.recycle()
        addView(surfaceView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        return if (canvas is BlurCanvas) {
            if (isShown && !dstRect.isEmpty) {
                val pixels = blurPixelsQueue.acquireToPrepare()
                if (pixels != null) {
                    pixels.prepare(canvas, locations)
                    pixels.recycle()
                    if (isBlurring.compareAndSet(false, true)) {
                        executor?.execute(blurRunnable)
                    }
                }
                try {
                    drawBitmap?.let { bm -> canvas.drawBitmap(bm, srcRect, dstRect, null) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            false
        } else {
            super.drawChild(canvas, child, drawingTime)
        }
    }

    override fun onViewAdded(child: View?) {
        assert(child === surfaceView)
        super.onViewAdded(child)
    }

    override fun onViewRemoved(child: View?) {
        assert(child !== surfaceView)
        super.onViewRemoved(child)
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        executor = Executors.newFixedThreadPool(2, object : ThreadFactory {
            val group = System.getSecurityManager()?.threadGroup ?: Thread.currentThread().threadGroup
            val id = AtomicInteger(0)
            override fun newThread(runnable: Runnable?): Thread = Thread(group, runnable, "BlurSurfaceView(${hashCode()})-${id.incrementAndGet()}")
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        val exe = executor
        executor = null
        if (exe != null) {
            exe.shutdown()
            try {
                if (!exe.awaitTermination(3, TimeUnit.SECONDS)) {
                    exe.shutdownNow()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                exe.shutdownNow()
            }
        }
    }
}