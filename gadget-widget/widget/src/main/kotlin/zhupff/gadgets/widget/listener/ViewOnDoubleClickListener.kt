package zhupff.gadgets.widget.listener

import android.view.View

abstract class ViewOnDoubleClickListener : ViewOnMultiClickListener() {

    final override fun onClick(v: View, count: Int): Long = when (count) {
        1 -> onFirstClick(v)
        2 -> onSecondClick(v).let { if (it > 0) -it else it }
        else -> DEFAULT_INTERVAL
    }

    /**
     * @return The interval(>0) before the second-click,
     * or the waiting-interval before the next-first-click.
     */
    open fun onFirstClick(v: View): Long = DEFAULT_INTERVAL

    /**
     * @return The waiting-interval(abs) before the next-first-click.
     */
    open fun onSecondClick(v: View): Long = -DEFAULT_INTERVAL
}