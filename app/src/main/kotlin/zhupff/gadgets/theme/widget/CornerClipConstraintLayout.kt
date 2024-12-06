package zhupff.gadgets.theme.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import zhupff.gadgets.theme.R
import kotlin.math.min

class CornerClipConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    var cornerRadius: Float = 0F
        set(value) {
            if (field != value) {
                field = value
                postInvalidate()
            }
        }
    var borderWidth: Float = 0F
        set(value) {
            if (field != value) {
                field = value
                borderPaint.strokeWidth = value * 2F
                postInvalidate()
            }
        }
    var borderColor: Int = Color.TRANSPARENT
        set(value) {
            if (field != value) {
                field = value
                borderPaint.color = value
                postInvalidate()
            }
        }

    private val borderPaint = Paint().also { paint ->
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth * 2F
        paint.color = borderColor
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.CornerClipConstraintLayout)
            .also { typedArray ->
                cornerRadius = typedArray.getDimension(R.styleable.CornerClipConstraintLayout_CornerClipRadius, cornerRadius)
                borderWidth = typedArray.getDimension(R.styleable.CornerClipConstraintLayout_BorderWidth, borderWidth)
                borderColor = typedArray.getColor(R.styleable.CornerClipConstraintLayout_BorderColor, borderColor)
            }
            .recycle()

        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val targetRadius = if (cornerRadius < 0F) min(width, height) / 2F else cornerRadius
                outline.setRoundRect(0, 0, width, height, targetRadius)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (borderWidth > 0F && borderColor != Color.TRANSPARENT) {
            val targetRadius = if (cornerRadius < 0F) min(width, height) / 2F else cornerRadius
            canvas.drawRoundRect(0F, 0F, width.toFloat(), height.toFloat(), targetRadius, targetRadius, borderPaint)
        }
    }
}