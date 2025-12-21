package gadget.widget.recyclerview

import android.content.Context
import android.graphics.PointF
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State

/**
 * 支持循环滚动的LayoutManager，继承自[LinearLayoutManager]。
 */
open class LoopLinearLayoutManager(
    context: Context,
    orientation: Int,
    reverseLayout: Boolean,
) : LinearLayoutManager(context, orientation, reverseLayout) {

    protected val dragListener = DragListener()

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        dragListener.attach(view)
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        // 用户拖拽的时候不处理，否则会无法循环。
        return if (dragListener.dragging) null else super.computeScrollVectorForPosition(targetPosition)
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: State): Int {
        loopHorizontally(dx, recycler, state)
        offsetChildrenHorizontal(-dx)
        return dx
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: State): Int {
        loopVertically(dy, recycler, state)
        offsetChildrenVertical(-dy)
        return dy
    }

    protected open fun loopHorizontally(dx: Int, recycler: Recycler, state: State) {
        if (state.isPreLayout || state.itemCount <= 0 || dx == 0) {
            return
        }
        if (dx > 0) {
            var tail = getChildAt(childCount - 1) ?: return
            val tailPosition = getPosition(tail)
            while (tail.right + paddingRight < width) {
                val position = (tailPosition + 1 + state.itemCount) % state.itemCount
                val scrap = recycler.getViewForPosition(position)
                addView(scrap)
                measureChildWithMargins(scrap, 0, 0)
                layoutDecorated(scrap, tail.right, paddingTop, tail.right + getDecoratedMeasuredWidth(scrap), paddingTop + getDecoratedMeasuredHeight(scrap))
                tail = scrap
            }
        } else/* if (dx < 0) */{
            var head = getChildAt(0) ?: return
            val headPosition = getPosition(head)
            while (head.left > paddingLeft) {
                val position = (headPosition - 1 + state.itemCount) % state.itemCount
                val scrap = recycler.getViewForPosition(position)
                addView(scrap, 0)
                measureChildWithMargins(scrap, 0, 0)
                layoutDecorated(scrap, head.left - getDecoratedMeasuredWidth(scrap), paddingTop, head.left, paddingTop + getDecoratedMeasuredHeight(scrap))
                head = scrap
            }
        }
    }

    protected open fun loopVertically(dy: Int, recycler: Recycler, state: State) {
        if (state.isPreLayout || state.itemCount <= 0 || dy == 0) {
            return
        }
        if (dy > 0) {
            var tail = getChildAt(childCount - 1) ?: return
            val tailPosition = getPosition(tail)
            while (tail.bottom + paddingBottom < height) {
                val position = (tailPosition + 1 + state.itemCount) % state.itemCount
                val scrap = recycler.getViewForPosition(position)
                addView(scrap)
                measureChildWithMargins(scrap, 0, 0)
                layoutDecorated(scrap, paddingLeft, tail.bottom, paddingLeft + getDecoratedMeasuredWidth(scrap), tail.bottom + getDecoratedMeasuredHeight(scrap))
                tail = scrap
            }
        } else/* if (dy < 0) */{
            var head = getChildAt(0) ?: return
            val headPosition = getPosition(head)
            while (head.top > paddingTop) {
                val position = (headPosition - 1 + state.itemCount) % state.itemCount
                val scrap = recycler.getViewForPosition(position)
                addView(scrap, 0)
                measureChildWithMargins(scrap, 0, 0)
                layoutDecorated(scrap, paddingLeft, head.top - getDecoratedMeasuredHeight(scrap), paddingLeft + getDecoratedMeasuredWidth(scrap), head.top)
                head = scrap
            }
        }
    }


    protected open class DragListener : RecyclerView.OnScrollListener() {
        /** 是否用户在拖拽 */
        var dragging: Boolean = false
            protected set
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            dragging = newState == RecyclerView.SCROLL_STATE_DRAGGING
        }

        open fun attach(recyclerView: RecyclerView?) {
            // 避免重复注册监听
            recyclerView?.removeOnScrollListener(this)
            recyclerView?.addOnScrollListener(this)
        }
    }
}