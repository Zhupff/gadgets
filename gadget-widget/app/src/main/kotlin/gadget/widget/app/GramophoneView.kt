package gadget.widget.app

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min
import kotlin.random.Random

class GramophoneView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val disc = Disc()

    private val drawer = Drawer()

    private val spinAnimator = SpinAnimator()

    val isSpinning: Boolean; get() = spinAnimator.isSpinning

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    fun startSpinning() {
        spinAnimator.start()
    }

    fun stopSpinning() {
        spinAnimator.stop()
    }

    fun resetSpinning() {
        spinAnimator.reset()
    }

    override fun onDraw(canvas: Canvas) {
        val c1 = canvas.save()
        canvas.rotate(spinAnimator.amplitude, disc.centerX, disc.centerY)
        canvas.drawCircle(disc.centerX, disc.centerY, disc.tableRadius, drawer.tablePaint)
        canvas.drawCircle(disc.centerX, disc.centerY, disc.tableRadius - drawer.tableBorderPaint.strokeWidth, drawer.tableBorderPaint)
        canvas.drawCircle(disc.centerX, disc.centerY, disc.discRadius, drawer.discBackgroundPaint)
        canvas.drawArc(
            disc.centerX - disc.discRadius,
            disc.centerY - disc.discRadius,
            disc.centerX + disc.discRadius,
            disc.centerY + disc.discRadius,
            0F, 360F, true, drawer.discLightPaint)
        var circle = disc.tableRadius * disc.picRadiusRatio
        while (circle < disc.discRadius) {
            canvas.drawCircle(disc.centerX, disc.centerY, circle, drawer.discPathPaint)
            circle += disc.discRadius * 0.04F
        }
        canvas.drawCircle(disc.centerX, disc.centerY, disc.discRadius, drawer.discBorderPaint)
        canvas.restoreToCount(c1)
        val c2 = canvas.save()
        canvas.clipPath(disc.picClipPath)
        canvas.scale(disc.picRadiusRatio, disc.picRadiusRatio, disc.centerX, disc.centerY)
        canvas.rotate(spinAnimator.rotate, disc.centerX, disc.centerY)
        super.onDraw(canvas)
        canvas.restoreToCount(c2)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            disc.onLayout()
            drawer.onLayout()
        }
    }

    override fun setScaleType(scaleType: ScaleType?) {
        assert(scaleType == ScaleType.CENTER_CROP)
        super.setScaleType(scaleType)
    }

    private inner class Drawer {
        val tablePaint = Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.FILL
            it.color = 0x66222222.toInt()
        }
        val tableBorderPaint = Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.STROKE
            it.color = 0x66FFFFFF.toInt()
        }
        val discBackgroundPaint = Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.FILL
            it.color = 0xFF222222.toInt()
        }
        val discBorderPaint = Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.STROKE
            it.color = 0xFF000000.toInt()
        }
        val discLightPaint = Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.FILL
        }
        val discPathPaint = Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.STROKE
        }
        private val lightColors = intArrayOf(
            0x99000000.toInt(),
            0x99999999.toInt(),
            0x99000000.toInt(),
            0x99000000.toInt(),
            0x99999999.toInt(),
            0x99000000.toInt(),
        )
        private val lightPositions = FloatArray(lightColors.size) { 1F / lightColors.size * it }
        fun onLayout() {
            tableBorderPaint.strokeWidth = disc.tableRadius / 200F
            discBorderPaint.strokeWidth = disc.discRadius / 100F
            val lightGradient = SweepGradient(disc.centerX, disc.centerY, lightColors, lightPositions)
            discLightPaint.shader = lightGradient
            discPathPaint.shader = lightGradient
            discPathPaint.strokeWidth = disc.discRadius / 300F
        }
    }


    private inner class Disc {

        var centerX: Float = 0F; private set
        var centerY: Float = 0F; private set
        var left: Float = 0F; private set
        var top: Float = 0F; private set
        var right: Float = 0F; private set
        var bottom: Float = 0F; private set
        var tableRadius: Float = 0F; private set
        var discRadius: Float = 0F; private set
        val picRadiusRatio: Float = 0.6F
        val picClipPath: Path = Path()

        fun onLayout() {
            left = paddingLeft.toFloat()
            top = paddingTop.toFloat()
            right = (width - paddingRight).toFloat()
            bottom = (height - paddingBottom).toFloat()
            centerX = (right + left) * 0.5F
            centerY = (bottom + top) * 0.5F
            tableRadius = min(right - left, bottom - top) / 2F
            discRadius = tableRadius * 0.92F
            picClipPath.reset()
            picClipPath.addCircle(disc.centerX, disc.centerY, tableRadius * picRadiusRatio , Path.Direction.CW)
        }
    }


    private inner class SpinAnimator : Animator.AnimatorListener, Animator.AnimatorPauseListener, ValueAnimator.AnimatorUpdateListener {

        var isSpinning: Boolean = false
            private set(value) {
                if (field != value) {
                    field = value
                }
            }

        var rotate: Float = 0F
            private set(value) {
                if (field != value) {
                    field = value
                    postInvalidate()
                }
            }

        var amplitude: Float = 0F
            get() {
                if (frequency > 15) {
                    frequency = 0
                    field = random.nextFloat()
                }
                if (isSpinning) {
                    frequency++
                }
                return field
            }

        private var frequency: Int = 0

        private val random = Random(hashCode())

        private val animator = ValueAnimator.ofFloat(0F, 360F).also { animator ->
            animator.repeatCount = Animation.INFINITE
            animator.duration = 24000L
            animator.interpolator = LinearInterpolator()
            animator.addListener(this)
            animator.addPauseListener(this)
            animator.addUpdateListener(this)
        }

        fun start() {
            if (isSpinning) return
            if (animator.isPaused) {
                animator.resume()
            } else {
                animator.start()
            }
        }

        fun stop() {
            if (!isSpinning) return
            animator.pause()
        }

        fun reset() {
            animator.cancel()
            rotate = 0F
        }

        override fun onAnimationStart(animation: Animator) {
            isSpinning = true
        }

        override fun onAnimationResume(animation: Animator) {
            isSpinning = true
        }

        override fun onAnimationPause(animation: Animator) {
            isSpinning = false
        }

        override fun onAnimationEnd(animation: Animator) {
            isSpinning = false
        }
        override fun onAnimationCancel(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}

        override fun onAnimationUpdate(animation: ValueAnimator) {
            rotate = animation.animatedValue as Float
        }
    }
}