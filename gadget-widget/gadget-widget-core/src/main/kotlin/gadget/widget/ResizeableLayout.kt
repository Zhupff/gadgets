package gadget.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import gadget.widget.attribute.ResizeStrategy
import gadget.widget.core.R

open class ResizeableLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr), ResizeStrategy {

    var resizeableRatio: Float = 0F
        set(value) {
            if (field != value) {
                field = value
                if (once) {
                    requestLayout()
                }
            }
        }

    var resizeStrategy: ResizeStrategy = ResizeStrategy.Default
        set(value) {
            if (field != value) {
                field = value
                if (once) {
                    resize()
                }
            }
        }

    @Volatile
    protected var once: Boolean = false

    init {
        context.withStyledAttributes(attrs, R.styleable.ResizeableLayout) {
            resizeableRatio = getFloat(R.styleable.ResizeableLayout_gadget_resizeable_ratio, resizeableRatio)
        }
        once = true
    }

    protected open fun resize() {
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        children.forEach { child ->
            resize(child, contentWidth, contentHeight, resizeableRatio)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        resize()
    }

    override fun resize(view: View, contentWidth: Int, contentHeight: Int, ratio: Float) {
        resizeStrategy.resize(view, contentWidth, contentHeight, ratio)
    }
}