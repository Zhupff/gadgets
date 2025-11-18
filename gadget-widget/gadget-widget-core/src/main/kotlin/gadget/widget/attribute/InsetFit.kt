package gadget.widget.attribute

import android.view.View
import android.view.WindowInsets
import androidx.core.view.WindowInsetsCompat

class InsetFit @JvmOverloads constructor(
    private val view: View,
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
) : View.OnApplyWindowInsetsListener {

    companion object {
        const val L = 1
        const val T = 2
        const val R = 4
        const val B = 8

        @JvmStatic
        fun get(view: View): InsetFit? = view.getTag(gadget.widget.core.R.id.gadget_inset_fit) as? InsetFit
    }

    var left: Boolean = false
        private set
    var top: Boolean = false
        private set
    var right: Boolean = false
        private set
    var bottom: Boolean = false
        private set

    @Volatile
    var once: Boolean = false
        private set

    init {
        if (view.getTag(gadget.widget.core.R.id.gadget_inset_fit) != null) {
            throw IllegalStateException("InsetFit already set!")
        } else {
            view.setTag(gadget.widget.core.R.id.gadget_inset_fit, this)
        }
        view.setOnApplyWindowInsetsListener(this)
        fit(left, top, right, bottom)
        once = true
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsets): WindowInsets {
        if (this.view !== v) {
            throw IllegalStateException("Not the same view!")
        }
        val windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, v)
        val systemBarInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
        val targetPaddingLeft = if (left) systemBarInsets.left else 0
        val targetPaddingTop = if (top) systemBarInsets.top else 0
        val targetPaddingRight = if (right) systemBarInsets.right else 0
        val targetPaddingBottom = if (bottom) systemBarInsets.bottom else 0
        if (v.paddingLeft != targetPaddingLeft ||
            v.paddingTop != targetPaddingTop ||
            v.paddingRight != targetPaddingRight ||
            v.paddingBottom != targetPaddingBottom) {
            v.setPadding(targetPaddingLeft, targetPaddingTop, targetPaddingRight, targetPaddingBottom)
        }
        return insets
    }

    fun fit(left: Boolean = this.left, top: Boolean = this.top, right: Boolean = this.right, bottom: Boolean = this.bottom) {
        if (left != this.left || top != this.top || right != this.right || bottom != this.bottom) {
            this.left = left
            this.top = top
            this.right = right
            this.bottom = bottom
            this.view.rootWindowInsets?.let {
                onApplyWindowInsets(this.view, it)
            }
        }
    }
}