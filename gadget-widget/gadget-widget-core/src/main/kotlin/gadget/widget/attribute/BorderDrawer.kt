package gadget.widget.attribute

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.withSave
import kotlin.math.max

class BorderDrawer<V : View> @JvmOverloads constructor(
    private val view: V,
    borderWidth: Float = 0F,
    borderShader: Drawable? = null,
    borderFit: Boolean = false,
) {

    var borderWidth: Float = borderWidth
        set(value) {
            val newValue = value.coerceAtLeast(0F)
            if (field != newValue) {
                field = newValue
                view.requestLayout()
            }
        }

    var borderShader: Drawable? = borderShader
        set(value) {
            if (field != value) {
                field = value
                view.postInvalidate()
            }
        }

    var borderFit: Boolean = borderFit
        set(value) {
            if (field != value) {
                field = value
                view.requestLayout()
            }
        }

    private val radius = FloatArray(8)
    private val path = Path()

    fun getRealBorderWidth(viewWidth: Int, viewHeight: Int): Float {
        if (borderWidth <= 0F) {
            return 0F
        }
        return minOf(viewWidth / 4F, viewHeight / 4F, this.borderWidth)
    }

    fun prepare(radius: FloatArray) {
        val viewWidth = view.width
        val viewHeight = view.height
        if (viewWidth <= 0 || viewHeight <= 0) {
            return
        }
        if (borderWidth < 0F || borderShader == null) {
            return
        }
        val padding = getRealBorderWidth(viewWidth, viewHeight)
        for (i in this.radius.indices) {
            this.radius[i] = max(0F, radius[i] - padding)
        }
        path.reset()
        path.addRoundRect(padding, padding, viewWidth - padding, viewHeight - padding, this.radius, Path.Direction.CW)
        val shaderWidth = borderShader!!.intrinsicWidth
        val shaderHeight = borderShader!!.intrinsicHeight
        if (shaderWidth <= 0 || shaderHeight <= 0) {
            if (viewWidth > viewHeight) {
                borderShader!!.setBounds(0, -(viewWidth - viewHeight) / 2, viewWidth, viewHeight + (viewWidth - viewHeight) / 2)
            } else {
                borderShader!!.setBounds(-(viewHeight - viewWidth) / 2, 0, viewWidth + (viewHeight - viewWidth) / 2, viewHeight)
            }
        } else {
            if (viewWidth * shaderHeight > viewHeight * shaderWidth) {
                val diff = if (viewWidth > shaderWidth) {
                    viewWidth * shaderHeight / shaderWidth - shaderHeight
                } else {
                    shaderHeight - viewWidth * shaderHeight / shaderWidth
                } / 2
                borderShader!!.setBounds(0, -diff, viewWidth, viewHeight + diff)
            } else {
                val diff = if (viewHeight > shaderHeight) {
                    viewHeight * shaderWidth / shaderHeight - shaderWidth
                } else {
                    shaderWidth - viewHeight * shaderWidth / shaderHeight
                } / 2
                borderShader!!.setBounds(-diff, 0, viewWidth + diff, viewHeight)
            }
        }
    }

    fun drawInner(canvas: Canvas, lambda: ((Canvas) -> Unit)? = null) {
        if (lambda != null) {
            canvas.withSave {
                clipPath(path)
                lambda(this)
            }
        }
    }

    fun drawOuter(canvas: Canvas, lambda: ((Canvas) -> Unit)? = null) {
        canvas.withSave {
            clipOutPath(path)
            lambda?.invoke(this)
            borderShader?.draw(this)
        }
    }
}