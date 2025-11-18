package gadget.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import gadget.widget.core.R
import androidx.core.content.withStyledAttributes
import gadget.widget.attribute.BorderDrawer
import gadget.widget.attribute.CornerClip
import gadget.widget.attribute.InsetFit

open class GadgetLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val borderDrawer: BorderDrawer

    private val cornerClip: CornerClip

    private val insetFit: InsetFit

    init {
        var cornerRadius: Float = 0F
        var cornerGravity: Int = CornerClip.N
        var borderWidth: Float = 0F
        var borderFit: Boolean = false
        var borderShader: Drawable? = null
        var fitLeft: Boolean = false
        var fitTop: Boolean = false
        var fitRight: Boolean = false
        var fitBottom: Boolean = false
        context.withStyledAttributes(attrs, R.styleable.GadgetLayout) {
            cornerRadius = getDimension(R.styleable.GadgetLayout_gadget_corner_radius, cornerRadius)
            cornerGravity = getInt(R.styleable.GadgetLayout_gadget_corner_gravity, cornerGravity)
            borderWidth = getDimension(R.styleable.GadgetLayout_gadget_border_width, borderWidth)
            borderFit = getBoolean(R.styleable.GadgetLayout_gadget_border_fit, borderFit)
            borderShader = getDrawable(R.styleable.GadgetLayout_gadget_border_shader)
            val fits = getInt(R.styleable.GadgetLayout_gadget_fit_window_insets, 0)
            fitLeft = fits and InsetFit.L != 0
            fitTop = fits and InsetFit.T != 0
            fitRight = fits and InsetFit.R != 0
            fitBottom = fits and InsetFit.B != 0
        }
        cornerClip = CornerClip(this, cornerRadius, cornerGravity)
        borderDrawer = BorderDrawer(this, borderWidth, borderShader, borderFit)
        insetFit = InsetFit(this, fitLeft, fitTop, fitRight, fitBottom)
    }

    var cornerRadius: Float
        get() = cornerClip.radius
        set(value) { cornerClip.radius = value }

    var cornerGravity: Int
        get() = cornerClip.gravity
        set(value) { cornerClip.gravity = value }

    var borderWidth: Float
        get() = borderDrawer.borderWidth
        set(value) { borderDrawer.borderWidth = value }

    var borderFit: Boolean
        get() = borderDrawer.borderFit
        set(value) { borderDrawer.borderFit = value }

    var borderShader: Drawable?
        get() = borderDrawer.borderShader
        set(value) { borderDrawer.borderShader = value }



    final override fun setClipToOutline(clipToOutline: Boolean) {
        if (!clipToOutline) {
            throw IllegalArgumentException("Not support!")
        }
        super.setClipToOutline(clipToOutline)
    }

    final override fun setOutlineProvider(provider: ViewOutlineProvider?) {
        if (this.outlineProvider == provider) {
            return
        }
        if (this.outlineProvider !is CornerClip && provider is CornerClip && !provider.once) {
            super.setOutlineProvider(provider)
            return
        }
        throw IllegalArgumentException("Not support!")
    }

    final override fun setForeground(foreground: Drawable?) {
        throw IllegalArgumentException("Not support!")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!borderFit || borderWidth <= 0F) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        // 这里是想让内容显示区域缩小，但不是通过padding或margin的方式。
        val widthMeasureSide = MeasureSpec.getSize(widthMeasureSpec)
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMeasureSide = MeasureSpec.getSize(heightMeasureSpec)
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
        val contentWidth = when (widthMeasureMode) {
            MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> widthMeasureSide
            else -> 0
        }
        val contentHeight = when (heightMeasureMode) {
            MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> heightMeasureSide
            else -> 0
        }
        if (contentWidth > 0 && contentHeight > 0) {
            val realBorderWidth = borderDrawer.adjustRealBorderWidth(contentWidth, contentHeight).toInt()
            super.onMeasure(
                MeasureSpec.makeMeasureSpec((contentWidth - realBorderWidth * 2).coerceAtLeast(0), widthMeasureMode),
                MeasureSpec.makeMeasureSpec((contentHeight - realBorderWidth * 2).coerceAtLeast(0), heightMeasureMode))
            setMeasuredDimension(measuredWidth + realBorderWidth * 2, measuredHeight + realBorderWidth * 2)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            if (measuredWidth > 0 && measuredHeight > 0) {
                val realBorderWidth = borderDrawer.adjustRealBorderWidth(measuredWidth, measuredHeight).toInt()
                super.onMeasure(
                    MeasureSpec.makeMeasureSpec((measuredWidth - realBorderWidth * 2).coerceAtLeast(0), widthMeasureMode),
                    MeasureSpec.makeMeasureSpec((measuredHeight - realBorderWidth * 2).coerceAtLeast(0), heightMeasureMode))
                setMeasuredDimension(measuredWidth + realBorderWidth * 2, measuredHeight + realBorderWidth * 2)
            }
        }
    }
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        if (!borderFit || borderWidth <= 0F) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//            return
//        }
//        // 这里是想让内容显示区域缩小，但不是通过padding或margin的方式。
//        val widthMeasureSide = MeasureSpec.getSize(widthMeasureSpec)
//        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
//        val heightMeasureSide = MeasureSpec.getSize(heightMeasureSpec)
//        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
//        val contentWidth = when (widthMeasureMode) {
//            MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> widthMeasureSide
//            else -> 0
//        }
//        val contentHeight = when (heightMeasureMode) {
//            MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> heightMeasureSide
//            else -> 0
//        }
//        if (contentWidth > 0 && contentHeight > 0) {
//            val realBorderWidth = borderDrawer.adjustRealBorderWidth(contentWidth, contentHeight).toInt()
//            super.onMeasure(
//                MeasureSpec.makeMeasureSpec((contentWidth - realBorderWidth * 2).coerceAtLeast(0), widthMeasureMode),
//                MeasureSpec.makeMeasureSpec((contentHeight - realBorderWidth * 2).coerceAtLeast(0), heightMeasureMode))
//            setMeasuredDimension(measuredWidth + realBorderWidth * 2, measuredHeight + realBorderWidth * 2)
//        } else {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//            if (measuredWidth > 0 && measuredHeight > 0) {
//                val realBorderWidth = borderDrawer.adjustRealBorderWidth(measuredWidth, measuredHeight).toInt()
//                super.onMeasure(
//                    MeasureSpec.makeMeasureSpec((measuredWidth - realBorderWidth * 2).coerceAtLeast(0), widthMeasureMode),
//                    MeasureSpec.makeMeasureSpec((measuredHeight - realBorderWidth * 2).coerceAtLeast(0), heightMeasureMode))
//                setMeasuredDimension(measuredWidth + realBorderWidth * 2, measuredHeight + realBorderWidth * 2)
//            }
//        }
//    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val realBorderWidth = borderDrawer.realBorderWidth.toInt()
        if (!borderFit || realBorderWidth <= 0F) {
            super.onLayout(changed, left, top, right, bottom)
            return
        }
        val parentL = paddingLeft + realBorderWidth
        val parentR = right - left - paddingRight - realBorderWidth
        val parentT = paddingTop + realBorderWidth
        val parentB = bottom - top - paddingBottom - realBorderWidth
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val childW = child.measuredWidth
                val childH = child.measuredHeight
                var childL = 0
                var childT = 0
                val childLayoutParams = child.layoutParams as LayoutParams
                var childGravity = childLayoutParams.gravity
                if (childGravity == -1) {
                    childGravity = Gravity.TOP or Gravity.START
                }
                val absoluteGravity = Gravity.getAbsoluteGravity(childGravity, layoutDirection)
                val verticalGravity = childGravity and Gravity.VERTICAL_GRAVITY_MASK
                childL = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                    Gravity.CENTER_HORIZONTAL -> {
                        parentL + (parentR - parentL - childW) / 2 + childLayoutParams.leftMargin - childLayoutParams.rightMargin
                    }
                    Gravity.RIGHT -> {
                        parentR - childW - childLayoutParams.rightMargin
                    }
                    else -> {
                        parentL + childLayoutParams.leftMargin
                    }
                }
                childT = when (verticalGravity) {
                    Gravity.TOP -> {
                        parentT + childLayoutParams.topMargin
                    }
                    Gravity.CENTER_VERTICAL -> {
                        parentT + (parentB - parentT - childH) / 2 + childLayoutParams.topMargin - childLayoutParams.bottomMargin
                    }
                    Gravity.BOTTOM -> {
                        parentB - childH - childLayoutParams.bottomMargin
                    }
                    else -> {
                        parentT + childLayoutParams.topMargin
                    }
                }
                child.layout(childL, childT, childL + childW, childT + childH)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (borderDrawer.realBorderWidth <= 0F) {
            super.dispatchDraw(canvas)
            return
        }
        borderDrawer.prepare(cornerClip.getRadiusArray())
        if (borderFit) {
            borderDrawer.drawInner(canvas) {
                super.dispatchDraw(canvas)
            }
        } else {
            super.dispatchDraw(canvas)
        }
        borderDrawer.drawOuter(canvas)
    }
}