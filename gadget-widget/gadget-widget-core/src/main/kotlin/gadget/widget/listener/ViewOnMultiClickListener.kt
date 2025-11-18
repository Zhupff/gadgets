package gadget.widget.listener

import android.os.SystemClock
import android.view.View
import gadget.widget.core.R
import kotlin.math.abs

abstract class ViewOnMultiClickListener : View.OnClickListener {

    companion object {
        const val DEFAULT_INTERVAL: Long = 500L
    }

    protected var interval: Long = DEFAULT_INTERVAL

    /**
     * @return The interval(millisecond) between this-click and next-click.
     */
    open fun onClick(v: View, count: Int): Long = DEFAULT_INTERVAL

    final override fun onClick(v: View?) {
        v ?: return
        val obj = v.getTag(R.id.gadget_view_multi_click_task)
        val task: ViewMultiCLickTask
        if (obj == null) {
            task = ViewMultiCLickTask(this)
            v.setTag(R.id.gadget_view_multi_click_task, task)
        } else if (obj is ViewMultiCLickTask) {
            if (obj.listener != this) {
                task = ViewMultiCLickTask(this)
                v.setTag(R.id.gadget_view_multi_click_task, task)
            } else {
                task = obj
            }
        } else {
            throw IllegalStateException("Invalid tag: $obj!")
        }
        val count = task.click(interval)
        if (count >= 0) {
            interval = onClick(v, count)
        }
    }

    private class ViewMultiCLickTask(
        /** [View.OnClickListener] delegate. */
        val listener: View.OnClickListener,
    ) {

        /** Click counter. */
        private var count: Int = 0

        /** The last-click timestamp(millisecond). */
        private var lastClickTimestamp: Long = 0L


        fun click(
            /** The interval(millisecond) between the last-click and this-click. */
            interval: Long = DEFAULT_INTERVAL,
        ): Int {
            val currentTimestamp = SystemClock.elapsedRealtime()
            if (interval > 0) {
                if (count <= 0) {
                    // First click
                    count = 0
                    lastClickTimestamp = currentTimestamp
                    return ++count
                } else {
                    if (currentTimestamp - lastClickTimestamp > interval) {
                        // timeout and restart
                        count = 0
                        return click()
                    } else {
                        lastClickTimestamp = currentTimestamp
                        return ++count
                    }
                }
            } else {
                // Stop continuous clicking,
                // and wait for abs(interval) before restarting the response to click-event.
                count = 0
                val absInterval = abs(interval)
                if (currentTimestamp - lastClickTimestamp > absInterval) {
                    // restart
                    return click()
                } else {
                    // waiting
                    return -1
                }
            }
        }
    }
}



inline fun View.onMultiClick(
    crossinline block: View.(Int) -> Long,
) {
    setOnClickListener(object : ViewOnMultiClickListener() {
        override fun onClick(v: View, count: Int): Long = block(v, count)
    })
}