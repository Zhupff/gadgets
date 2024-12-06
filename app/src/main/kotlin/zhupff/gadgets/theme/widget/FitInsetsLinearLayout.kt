package zhupff.gadgets.theme.widget

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import zhupff.gadgets.theme.R

class FitInsetsLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var fitTopInset: Boolean = false
    private var fitBottomInset: Boolean = false

    init {
        context.obtainStyledAttributes(attrs, R.styleable.FitInsetsLinearLayout)
            .also { typedArray ->
                fitTopInset = typedArray.getBoolean(R.styleable.FitInsetsLinearLayout_fitTopInset, fitTopInset)
                fitBottomInset = typedArray.getBoolean(R.styleable.FitInsetsLinearLayout_fitBottomInset, fitBottomInset)
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