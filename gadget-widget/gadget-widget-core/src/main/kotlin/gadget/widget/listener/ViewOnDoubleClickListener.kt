package gadget.widget.listener

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



inline fun View.onDoubleClick(
    crossinline first: View.() -> Long,
    crossinline second: View.() -> Long,
) {
    setOnClickListener(object : ViewOnDoubleClickListener() {
        override fun onFirstClick(v: View): Long = first(v)
        override fun onSecondClick(v: View): Long = second(v)
    })
}