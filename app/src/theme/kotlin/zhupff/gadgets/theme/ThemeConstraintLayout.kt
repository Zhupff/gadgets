package zhupff.gadgets.theme

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlin.math.min

class ThemeConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), ThemeDispatcher, Observer<Theme> {

    private val themeLiveData: MutableLiveData<Theme> = MutableLiveData()

    var theme: Theme?
        set(value) {
            val current = themeLiveData.value
            if (current != value) {
                themeLiveData.postValue(value)
            }
        }
        get() = themeLiveData.value

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
        context.obtainStyledAttributes(attrs, R.styleable.ThemeConstraintLayout)
            .also { typedArray ->
                cornerRadius = typedArray.getDimension(R.styleable.ThemeConstraintLayout_CornerClipRadius, cornerRadius)
                borderWidth = typedArray.getDimension(R.styleable.ThemeConstraintLayout_BorderWidth, borderWidth)
                borderColor = typedArray.getColor(R.styleable.ThemeConstraintLayout_BorderColor, borderColor)
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

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (borderWidth > 0F && borderColor != Color.TRANSPARENT) {
            val targetRadius = if (cornerRadius < 0F) min(width, height) / 2F else cornerRadius
            canvas.drawRoundRect(0F, 0F, width.toFloat(), height.toFloat(), targetRadius, targetRadius, borderPaint)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        themeLiveData.observeForever(this)
        ThemeUtil.current.observeForever(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        themeLiveData.removeObserver(this)
        ThemeUtil.current.removeObserver(this)
    }

    override fun observableTheme(): LiveData<Theme> = themeLiveData

    override fun onChanged(currentTheme: Theme?) {
        if (ThemeUtil.current.value == theme) {
            findViewById<View>(R.id.Check).isVisible = true
        } else {
            findViewById<View>(R.id.Check).isInvisible = true
        }

        findViewById<View>(R.id.Mask).isVisible = theme == null
    }
}