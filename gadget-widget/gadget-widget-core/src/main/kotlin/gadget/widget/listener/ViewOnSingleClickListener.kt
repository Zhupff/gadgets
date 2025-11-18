package gadget.widget.listener

import android.view.View

abstract class ViewOnSingleClickListener : ViewOnMultiClickListener() {

    final override fun onClick(v: View, count: Int): Long = onSingleClick(v).let { if (it > 0) -it else it }

    /**
     * @return The waiting-interval(abs) before the next-click.
     */
    open fun onSingleClick(v: View): Long = -DEFAULT_INTERVAL
}



inline fun View.onSingleClick(
    crossinline block: View.() -> Long,
) {
    setOnClickListener(object : ViewOnSingleClickListener() {
        override fun onSingleClick(v: View): Long = block(v)
    })
}