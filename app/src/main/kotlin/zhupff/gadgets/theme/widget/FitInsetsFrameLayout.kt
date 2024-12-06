package zhupff.gadgets.theme.widget

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import zhupff.gadgets.theme.R

class FitInsetsFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var fitTopInset: Boolean = false
    private var fitBottomInset: Boolean = false

    init {
        context.obtainStyledAttributes(attrs, R.styleable.FitInsetsFrameLayout)
            .also { typedArray ->
                fitTopInset = typedArray.getBoolean(R.styleable.FitInsetsFrameLayout_fitTopInset, fitTopInset)
                fitBottomInset = typedArray.getBoolean(R.styleable.FitInsetsFrameLayout_fitBottomInset, fitBottomInset)
            }
            .recycle()
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        val insetsCompat = ViewCompat.getRootWindowInsets(this)!!.getInsets(WindowInsetsCompat.Type.systemBars())
        val targetPaddingTop = if (fitTopInset) insetsCompat.top else 0
        val targetPaddingBottom = if (fitBottomInset) insetsCompat.bottom else 0
        if (paddingTop != targetPaddingTop || paddingBottom != targetPaddingBottom) {
            setPadding(paddingLeft, targetPaddingTop, paddingRight, targetPaddingBottom)
        }
        return insets
    }
}