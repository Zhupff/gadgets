package zhupff.gadgets.widget.insetfit

import android.view.View
import android.view.WindowInsets
import androidx.core.view.WindowInsetsCompat
import zhupff.gadgets.widget.R

class InsetFit @JvmOverloads constructor(
    val target: View,
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
) : View.OnApplyWindowInsetsListener {

    companion object {
        @JvmStatic
        fun get(target: View): InsetFit? = target.getTag(R.id.gadgets_view_inset_fit) as? InsetFit

        @JvmStatic
        fun reset(target: View) {
            target.setTag(R.id.gadgets_view_inset_fit, null)
            target.setOnApplyWindowInsetsListener(null)
        }
    }

    var left: Boolean = left; private set
    var top: Boolean = top; private set
    var right: Boolean = right; private set
    var bottom: Boolean = bottom; private set

    init {
        target.setTag(R.id.gadgets_view_inset_fit, this)
        target.setOnApplyWindowInsetsListener(this)
        target.rootWindowInsets?.let { insets ->
            onApplyWindowInsets(target, insets)
        }
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsets): WindowInsets {
        if (v === target && get(v) === this) {
            WindowInsetsCompat.toWindowInsetsCompat(insets).let { windowInsetsCompat ->
                val systemBarsInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
                val targetPaddingLeft = if (left) systemBarsInsets.left else 0
                val targetPaddingTop = if (top) systemBarsInsets.top else 0
                val targetPaddingRight = if (right) systemBarsInsets.right else 0
                val targetPaddingBottom = if (bottom) systemBarsInsets.bottom else 0
                if (v.paddingLeft != targetPaddingLeft ||
                    v.paddingTop != targetPaddingTop ||
                    v.paddingRight != targetPaddingRight ||
                    v.paddingBottom != targetPaddingBottom) {
                    v.setPadding(targetPaddingLeft, targetPaddingTop, targetPaddingRight, targetPaddingBottom)
                }
            }
        }
        return insets
    }

    fun fit(left: Boolean, top: Boolean, right: Boolean, bottom: Boolean) {
        if (this.left != left || this.top != top || this.right != right || this.bottom != bottom) {
            this.left = left
            this.top = top
            this.right = right
            this.bottom = bottom
            target.rootWindowInsets?.let { insets ->
                onApplyWindowInsets(target, insets)
            }
        }
    }
}

var View.insetFit: InsetFit?
    get() = InsetFit.get(this)
    set(value) {
        if (value == null) {
            InsetFit.reset(this)
        } else {
            if (value !== InsetFit.get(this)) {
                throw IllegalArgumentException("Use InsetFit constructor instead!")
            }
        }
    }