package gadget.widget.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import gadget.widget.LayoutParamsDSL
import gadget.widget.WidgetDSL

@WidgetDSL
class TestLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    @WidgetDSL
    private lateinit var frameLayout: FrameLayout

    @LayoutParamsDSL("frameLayoutParams")
    private lateinit var frameLayoutParams: FrameLayout.LayoutParams

    @WidgetDSL
    class TestView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr)
}