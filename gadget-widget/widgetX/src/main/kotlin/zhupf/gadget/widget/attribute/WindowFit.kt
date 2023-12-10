package zhupf.gadget.widget.attribute

import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import androidx.core.view.WindowInsetsCompat
import zhupf.gadget.widget.x.R

private const val _L = 0
private const val _T = 1
private const val _R = 2
private const val _B = 3

interface WindowFit {
    val windowFit: WindowFitDelegate
}

class WindowFitDelegate(
    private val view: View,
    attributeSet: AttributeSet? = null,
) {

    private val currentFits: BooleanArray = BooleanArray(4)
    private val fitLengths: IntArray = IntArray(4)

    init {
        view.context.obtainStyledAttributes(attributeSet, R.styleable.WindowFit).also { typedArray ->
            val fitWindow = typedArray.getBoolean(R.styleable.WindowFit_fitWindow, false)
            val fitWindowHorizontal = typedArray.getBoolean(R.styleable.WindowFit_fitWindowHorizontal, fitWindow)
            val fitWindowVertical = typedArray.getBoolean(R.styleable.WindowFit_fitWindowVertical, fitWindow)
            val fitWindowLeft = typedArray.getBoolean(R.styleable.WindowFit_fitWindowLeft, fitWindowHorizontal)
            val fitWindowTop = typedArray.getBoolean(R.styleable.WindowFit_fitWindowTop, fitWindowVertical)
            val fitWindowRight = typedArray.getBoolean(R.styleable.WindowFit_fitWindowRight, fitWindowHorizontal)
            val fitWindowBottom = typedArray.getBoolean(R.styleable.WindowFit_fitWindowBottom, fitWindowVertical)

            booleanArrayOf(
                fitWindowLeft, fitWindowTop, fitWindowRight, fitWindowBottom,
            ).copyInto(currentFits)
        }.recycle()
    }

    var fitWindowLeft: Boolean
        get() = currentFits[_L]
        set(value) {
            if (value != currentFits[_L]) {
                currentFits[_L] = value
                invalidate()
            }
        }

    var fitWindowTop: Boolean
        get() = currentFits[_T]
        set(value) {
            if (value != currentFits[_T]) {
                currentFits[_T] = value
                invalidate()
            }
        }

    var fitWindowRight: Boolean
        get() = currentFits[_R]
        set(value) {
            if (value != currentFits[_R]) {
                currentFits[_R] = value
                invalidate()
            }
        }

    var fitWindowBottom: Boolean
        get() = currentFits[_B]
        set(value) {
            if (value != currentFits[_B]) {
                currentFits[_B] = value
                invalidate()
            }
        }

    fun onWindowInsetsApplied(insets: WindowInsets?) {
        insets ?: return
        val compatInsets = WindowInsetsCompat.toWindowInsetsCompat(insets, view)
            .getInsets(WindowInsetsCompat.Type.systemBars())
        fitLengths[_L] = compatInsets.left
        fitLengths[_T] = compatInsets.top
        fitLengths[_R] = compatInsets.right
        fitLengths[_B] = compatInsets.bottom
        invalidate()
    }

    private fun invalidate() {
        val targetPaddingLeft  = if (currentFits[_L]) fitLengths[_L] else 0
        val targetPaddingTop = if (currentFits[_T]) fitLengths[_T] else 0
        val targetPaddingRight = if (currentFits[_R]) fitLengths[_R] else 0
        val targetPaddingBottom = if (currentFits[_B]) fitLengths[_B] else 0
        if (view.paddingLeft != targetPaddingLeft ||
            view.paddingTop != targetPaddingTop ||
            view.paddingRight != targetPaddingRight ||
            view.paddingBottom != targetPaddingBottom) {
            view.setPadding(targetPaddingLeft, targetPaddingTop, targetPaddingRight, targetPaddingBottom)
        }
    }
}