package zhupf.gadget.widget.common

import android.os.SystemClock
import android.view.View

abstract class ViewOnMultiClickListener : View.OnClickListener {

    private val intervals: LongArray

    constructor(count: Int) {
        intervals = LongArray(count) { 500L }
    }

    constructor(intervals: LongArray) {
        this.intervals = intervals
    }

    final override fun onClick(v: View?) {
        v ?: return
        val obj = v.getTag(R.id.gadget_widget_view_multi_click_task)
        val task: ClickTask
        if (obj == null) {
            task = ClickTask(this, this.intervals)
            v.setTag(R.id.gadget_widget_view_multi_click_task, task)
        } else if (obj is ClickTask) {
            if (obj.listener != this) {
                task = ClickTask(this, this.intervals)
                v.setTag(R.id.gadget_widget_view_multi_click_task, task)
            } else {
                task = obj
            }
        } else {
            throw IllegalStateException("Invalid tag: $obj")
        }
        val count = task.click()
        if (count >= 0) {
            onClick(count, v)
        }
    }

    open fun onClick(count: Int, v: View?) {}

    private class ClickTask(
        val listener: View.OnClickListener,
        val intervals: LongArray,
    ) {
        private var count: Int = 0

        private var lastClickTimestamp = 0L

        fun click(): Int {
            val currentTimestamp = SystemClock.elapsedRealtime()
            if (count == 0) {
                // first click
                lastClickTimestamp = currentTimestamp
                return ++count
            } else {
                val interval = this.intervals[count - 1]
                if (currentTimestamp - lastClickTimestamp > interval) {
                    count = 0
                    click()
                } else {
                    if (count < this.intervals.size) {
                        lastClickTimestamp = currentTimestamp
                        return ++count
                    }
                }
            }
            return -1
        }
    }
}