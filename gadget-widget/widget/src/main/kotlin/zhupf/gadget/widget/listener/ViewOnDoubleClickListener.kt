package zhupf.gadget.widget.listener

import android.view.View

abstract class ViewOnDoubleClickListener(
    interval1: Long = 500L,
    interval2: Long = 500L,
) : ViewOnMultiClickListener(longArrayOf(interval1, interval2)) {

    final override fun onClick(count: Int, v: View?) {
        super.onClick(count, v)
        if (count == 1) {
            onFirstClick(v)
        } else if (count == 2) {
            onSecondClick(v)
        }
    }

    open fun onFirstClick(v: View?) {}

    open fun onSecondClick(v: View?) {}
}