package zhupf.gadget.widget.attribute

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import zhupf.gadget.widget.R

private const val _TLX = 0
private const val _TLY = 1
private const val _TRX = 2
private const val _TRY = 3
private const val _BRX = 4
private const val _BRY = 5
private const val _BLX = 6
private const val _BLY = 7

private const val _BC = 0
private const val _BW = 1

interface CornerClip {
    val cornerClip: CornerClipDelegate
}

class CornerClipDelegate(
    private val view: View,
    attributeSet: AttributeSet? = null,
) {

    private val currentRadius: FloatArray = FloatArray(8)
    private val pendingRadius: FloatArray = FloatArray(8)
    private var currentBorder: Array<Number> = arrayOf(Color.TRANSPARENT, 0F)
    private var pendingBorder: Array<Number> = arrayOf(Color.TRANSPARENT, 0F)

    init {
        view.context.obtainStyledAttributes(attributeSet, R.styleable.CornerClip).also { typedArray ->
            val allCornerRadius = typedArray.getDimension(R.styleable.CornerClip_allCornerRadius, 0F)

            val tlCornerRadius = typedArray.getDimension(R.styleable.CornerClip_tlCornerRadius, allCornerRadius)
            val tlCornerRadiusX = typedArray.getDimension(R.styleable.CornerClip_tlCornerRadiusX, tlCornerRadius)
            val tlCornerRadiusY = typedArray.getDimension(R.styleable.CornerClip_tlCornerRadiusY, tlCornerRadius)

            val trCornerRadius = typedArray.getDimension(R.styleable.CornerClip_trCornerRadius, allCornerRadius)
            val trCornerRadiusX = typedArray.getDimension(R.styleable.CornerClip_trCornerRadiusX, trCornerRadius)
            val trCornerRadiusY = typedArray.getDimension(R.styleable.CornerClip_trCornerRadiusY, trCornerRadius)

            val brCornerRadius = typedArray.getDimension(R.styleable.CornerClip_brCornerRadius, allCornerRadius)
            val brCornerRadiusX = typedArray.getDimension(R.styleable.CornerClip_brCornerRadiusX, brCornerRadius)
            val brCornerRadiusY = typedArray.getDimension(R.styleable.CornerClip_brCornerRadiusY, brCornerRadius)

            val blCornerRadius = typedArray.getDimension(R.styleable.CornerClip_blCornerRadius, allCornerRadius)
            val blCornerRadiusX = typedArray.getDimension(R.styleable.CornerClip_blCornerRadiusX, blCornerRadius)
            val blCornerRadiusY = typedArray.getDimension(R.styleable.CornerClip_blCornerRadiusY, blCornerRadius)

            floatArrayOf(
                tlCornerRadiusX, tlCornerRadiusY,
                trCornerRadiusX, trCornerRadiusY,
                brCornerRadiusX, brCornerRadiusY,
                blCornerRadiusX, blCornerRadiusY,
            ).run {
                copyInto(currentRadius)
                copyInto(pendingRadius)
            }

            val borderColor = typedArray.getColor(R.styleable.CornerClip_borderColor, Color.TRANSPARENT)
            val borderWidth = typedArray.getDimension(R.styleable.CornerClip_borderWidth, 0F)

            arrayOf(borderColor, borderWidth).run {
                copyInto(currentBorder)
                copyInto(pendingBorder)
            }
        }.recycle()

        view.addOnLayoutChangeListener { _, l1, t1, r1, b1, l2, t2, r2, b2 ->
            if (r1 - l1 != r2 - l2 || b1 - t1 != b2 - t2) {
                cornerRect.set(0F, 0F, view.width.toFloat(), view.height.toFloat())
                cornerPath.reset()
                cornerPath.addRoundRect(cornerRect, currentRadius, Path.Direction.CW)
            }
        }
    }

    var tlCornerRadius: Pair<Float, Float>
        get() = tlCornerRadiusX to tlCornerRadiusY
        set(value) {
            tlCornerRadiusX = value.first
            tlCornerRadiusY = value.second
        }

    var trCornerRadius: Pair<Float, Float>
        get() = trCornerRadiusX to trCornerRadiusY
        set(value) {
            trCornerRadiusX = value.first
            trCornerRadiusY = value.second
        }

    var brCornerRadius: Pair<Float, Float>
        get() = brCornerRadiusX to brCornerRadiusY
        set(value) {
            brCornerRadiusX = value.first
            brCornerRadiusY = value.second
        }

    var blCornerRadius: Pair<Float, Float>
        get() = blCornerRadiusX to blCornerRadiusY
        set(value) {
            blCornerRadiusX = value.first
            blCornerRadiusY = value.second
        }

    var tlCornerRadiusX: Float
        get() = currentRadius[_TLX]
        set(value) {
            pendingRadius[_TLX] = value
            view.postInvalidate()
        }

    var tlCornerRadiusY: Float
        get() = currentRadius[_TLY]
        set(value) {
            pendingRadius[_TLY] = value
            view.postInvalidate()
        }

    var trCornerRadiusX: Float
        get() = currentRadius[_TRX]
        set(value) {
            pendingRadius[_TRX] = value
            view.postInvalidate()
        }

    var trCornerRadiusY: Float
        get() = currentRadius[_TRY]
        set(value) {
            pendingRadius[_TRY] = value
            view.postInvalidate()
        }

    var brCornerRadiusX: Float
        get() = currentRadius[_BRX]
        set(value) {
            pendingRadius[_BRX] = value
            view.postInvalidate()
        }

    var brCornerRadiusY: Float
        get() = currentRadius[_BRY]
        set(value) {
            pendingRadius[_BRY] = value
            view.postInvalidate()
        }

    var blCornerRadiusX: Float
        get() = currentRadius[_BLX]
        set(value) {
            pendingRadius[_BLX] = value
            view.postInvalidate()
        }

    var blCornerRadiusY: Float
        get() = currentRadius[_BLY]
        set(value) {
            pendingRadius[_BLY] = value
            view.postInvalidate()
        }

    var borderColor: Int
        get() = currentBorder[_BC] as Int
        set(value) {
            pendingBorder[_BC] = value
            view.postInvalidate()
        }

    var borderWidth: Float
        get() = currentBorder[_BW] as Float
        set(value) {
            pendingBorder[_BW] = value
            view.postInvalidate()
        }

    private val cornerRect: RectF = RectF()
    private val cornerPath: Path = Path()
    private val borderPaint: Paint = Paint().also {
        it.isAntiAlias = true
        it.color = borderColor
        it.style = Paint.Style.STROKE
        it.strokeWidth = borderWidth
    }

    fun clipCorner(canvas: Canvas) {
        if (!pendingRadius.contentEquals(currentRadius)) {
            pendingRadius.copyInto(currentRadius)
            cornerPath.reset()
            cornerPath.addRoundRect(cornerRect, currentRadius, Path.Direction.CW)
        }
        if (currentRadius.any { it > 0F }) {
            canvas.clipPath(cornerPath)
        }
    }

    fun drawBorder(canvas: Canvas) {
        if (!pendingBorder.contentEquals(currentBorder)) {
            pendingBorder.copyInto(currentBorder)
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidth
        }
        if (borderColor != Color.TRANSPARENT && borderWidth > 0F) {
            canvas.drawPath(cornerPath, borderPaint)
        }
    }
}