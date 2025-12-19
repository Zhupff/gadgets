package gadget.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.withStyledAttributes

open class FitSideImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        const val L = 1
        const val T = 2
        const val R = 4
        const val B = 8
    }

    var fitSide: Int = T
        set(value) {
            if (value != L && value != T && value != R && value != B) {
                throw IllegalArgumentException("Not support ${value}!")
            }
            if (field != value) {
                field = value
                if (once) {
                    postInvalidate()
                }
            }
        }

    @Volatile
    protected var once: Boolean = false
        private set

    init {
        context.withStyledAttributes(attrs, gadget.widget.core.R.styleable.FitSideImageView) {
            fitSide = getInt(gadget.widget.core.R.styleable.FitSideImageView_gadget_fit_side, T)
        }
        scaleType = ScaleType.MATRIX
        once = true
    }

    final override fun setScaleType(scaleType: ScaleType?) {
        if (scaleType != ScaleType.MATRIX) throw IllegalArgumentException("Not support ${scaleType}!")
        super.setScaleType(scaleType)
    }

    override fun onDraw(canvas: Canvas) {
        drawable?.let {
            val m = imageMatrix
            if (fitSide == T || fitSide == B) {
                val s = width.toFloat() / it.intrinsicWidth.toFloat()
                m.setScale(s, s)
                m.postTranslate(0F, if (fitSide == T) 0F else height.toFloat() - it.intrinsicHeight.toFloat() * s)
            } else {
                val s = height.toFloat() / it.intrinsicHeight.toFloat()
                m.setScale(s, s)
                m.postTranslate(if (fitSide == L) 0F else width.toFloat() - it.intrinsicWidth.toFloat() * s, 0F)
            }
            imageMatrix = m
        }
        super.onDraw(canvas)
    }
}