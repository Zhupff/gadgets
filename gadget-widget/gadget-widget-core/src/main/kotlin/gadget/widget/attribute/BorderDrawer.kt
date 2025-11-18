package gadget.widget.attribute

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.withSave
import kotlin.math.max

class BorderDrawer @JvmOverloads constructor(
    private val view: View,
    borderWidth: Float = 0F,
    borderShader: Drawable? = null,
    borderFit: Boolean = false,
) {

    companion object {
        @JvmStatic
        fun get(view: View): BorderDrawer? = view.getTag(gadget.widget.core.R.id.gadget_border_drawer) as? BorderDrawer
    }

    var borderWidth: Float = borderWidth
        set(value) {
            val newValue = value.coerceAtLeast(0F)
            if (field != newValue) {
                field = newValue
                view.requestLayout()
            }
        }
    var realBorderWidth: Float = borderWidth
        private set

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

    @Volatile
    var once: Boolean = false
        private set

    private val radius = FloatArray(8)
    private val path = Path()

    init {
        if (view.getTag(gadget.widget.core.R.id.gadget_border_drawer) != null) {
            throw IllegalStateException("BorderDrawer already set!")
        } else {
            view.setTag(gadget.widget.core.R.id.gadget_border_drawer, this)
        }
        once = true
    }

    fun adjustRealBorderWidth(contentWidth: Int, contentHeight: Int): Float {
        realBorderWidth = if (borderWidth <= 0F) {
            0F
        } else {
            minOf(contentWidth / 4F, contentHeight / 4F, this.borderWidth)
        }
        return realBorderWidth
    }

    fun prepare(radius: FloatArray) {
        val viewWidth = view.width
        val viewHeight = view.height
        if (viewWidth <= 0 || viewHeight <= 0) {
            return
        }
        if (realBorderWidth < 0F || borderShader == null) {
            return
        }
        for (i in this.radius.indices) {
            this.radius[i] = max(0F, radius[i] - realBorderWidth)
        }
        path.reset()
        path.addRoundRect(realBorderWidth, realBorderWidth, viewWidth - realBorderWidth, viewHeight - realBorderWidth, this.radius, Path.Direction.CW)
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