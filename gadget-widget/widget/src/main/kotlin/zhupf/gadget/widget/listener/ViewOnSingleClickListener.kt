package zhupf.gadget.widget.listener

import android.view.View

abstract class ViewOnSingleClickListener(
    interval: Long = 500L,
): ViewOnMultiClickListener(longArrayOf(interval)) {

    final override fun onClick(count: Int, v: View?) {
        super.onClick(count, v)
        onSingleClick(v)
    }

    open fun onSingleClick(v: View?) {}
}