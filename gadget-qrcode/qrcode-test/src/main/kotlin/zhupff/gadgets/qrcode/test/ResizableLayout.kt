package zhupff.gadgets.qrcode.test

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import kotlin.math.roundToInt

class ResizableLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object {
        val DEFAULT_RESIZE_STRATEGY = object : ResizeStrategy {
            override fun resize(child: View, parentWidth: Int, parentHeight: Int, ratio: Float) {
                if (parentWidth <= 0 || parentHeight <= 0) {
                    return
                }
                if (ratio < 0F) {
                    child.isVisible = false
                } else {
                    child.isVisible = true
                    val layoutParams = child.layoutParams as LayoutParams
                    if (ratio == 0F) {
                        layoutParams.width = LayoutParams.MATCH_PARENT
                        layoutParams.height = LayoutParams.MATCH_PARENT
                    } else {
                        val parentRatio = parentWidth.toFloat() / parentHeight.toFloat()
                        if (parentRatio < ratio) {
                            layoutParams.width = parentWidth
                            layoutParams.height = (parentWidth / ratio).roundToInt()
                        } else if (parentRatio > ratio) {
                            layoutParams.height = parentHeight
                            layoutParams.width = (parentHeight * ratio).roundToInt()
                        } else {
                            layoutParams.width = parentWidth
                            layoutParams.height = parentHeight
                        }
                    }
                    layoutParams.gravity = Gravity.CENTER
                    child.layoutParams = layoutParams
                }
            }
        }
    }

    interface ResizeStrategy {
        fun resize(child: View, parentWidth: Int, parentHeight: Int, ratio: Float)
    }

    var ratio: Float = 0F
        set(value) {
            if (field != value) {
                field = value
                resize()
            }
        }

    var strategy: ResizeStrategy = DEFAULT_RESIZE_STRATEGY
        set(value) {
            if (field != value) {
                field = value
                resize()
            }
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            resize()
        }
    }

    private fun resize() {
        for (child in children) {
            strategy.resize(child, width, height, ratio)
        }
    }
}