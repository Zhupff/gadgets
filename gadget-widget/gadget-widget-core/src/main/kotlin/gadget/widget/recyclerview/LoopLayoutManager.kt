package gadget.widget.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State
import androidx.recyclerview.widget.RecyclerView.VERTICAL

/**
 * 支持循环滚动的LayoutManager，继承自[RecyclerView.LayoutManager]，主要是学习用的，有需要建议使用[LoopLinearLayoutManager]。
 */
open class LoopLayoutManager(
    orientation: Int,
) : RecyclerView.LayoutManager() {

    init {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw IllegalArgumentException("Invalid orientation: ${orientation}!")
        }
    }

    @RecyclerView.Orientation
    var orientation: Int = orientation
        set(value) {
            if (field != value) {
                if (value != HORIZONTAL && value != VERTICAL) {
                    throw IllegalArgumentException("Invalid orientation: ${value}!")
                }
                field = value
                requestLayout()
            }
        }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)

    override fun canScrollHorizontally(): Boolean = this.orientation == HORIZONTAL

    override fun canScrollVertically(): Boolean = this.orientation == VERTICAL

    override fun onLayoutChildren(recycler: Recycler, state: State) {
        if (state.isPreLayout || state.itemCount <= 0) {
            removeAndRecycleAllViews(recycler)
            return
        }
        detachAndScrapAttachedViews(recycler)
        if (orientation == HORIZONTAL) {
            onLayoutChildrenHorizontally(recycler, state)
        } else/* if (orientation == VERTICAL) */{
            onLayoutChildrenVertically(recycler, state)
        }
    }

    private fun onLayoutChildrenHorizontally(recycler: Recycler, state: State) {
        var left = paddingLeft
        val top = paddingTop
        var index = 0
        while (left + paddingRight <= width) {
            val child = recycler.getViewForPosition(index)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            val right = left + getDecoratedMeasuredWidth(child)
            val bottom = top + getDecoratedMeasuredHeight(child)
            layoutDecorated(child, left, top, right, bottom)
            left = right
            index = (index + 1) % state.itemCount
        }

//        for (index in 0 until state.itemCount) {
//            if (left + paddingRight > width) {
//                break
//            }
//            val child = recycler.getViewForPosition(index)
//            addView(child)
//            measureChildWithMargins(child, 0, 0)
//            val right = left + getDecoratedMeasuredWidth(child)
//            val bottom = top + getDecoratedMeasuredHeight(child)
//            layoutDecorated(child, left, top, right, bottom)
//            left = right
//        }
    }

    private fun onLayoutChildrenVertically(recycler: Recycler, state: State) {
        val left = paddingLeft
        var top = paddingTop
        var index = 0
        while (top + paddingBottom <= height) {
            val child = recycler.getViewForPosition(index)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            val right = left + getDecoratedMeasuredWidth(child)
            val bottom = top + getDecoratedMeasuredHeight(child)
            layoutDecorated(child, left, top, right, bottom)
            top = bottom
            index = (index + 1) % state.itemCount
        }

//        for (index in 0 until state.itemCount) {
//            if (top + paddingBottom > height) {
//                break
//            }
//            val child = recycler.getViewForPosition(index)
//            addView(child)
//            measureChildWithMargins(child, 0, 0)
//            val right = left + getDecoratedMeasuredWidth(child)
//            val bottom = top + getDecoratedMeasuredHeight(child)
//            layoutDecorated(child, left, top, right, bottom)
//            top = bottom
//        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: State): Int {
        loopHorizontally(dx, recycler, state)
        offsetChildrenHorizontal(-dx)
        recycleChildren(recycler, state)
        return dx
    }

    private fun loopHorizontally(dx: Int, recycler: Recycler, state: State) {
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

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: State): Int {
        loopVertically(dy, recycler, state)
        offsetChildrenVertical(-dy)
        recycleChildren(recycler, state)
        return dy
    }

    private fun loopVertically(dy: Int, recycler: Recycler, state: State) {
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

    private fun recycleChildren(recycler: Recycler, state: State) {
        if (state.isPreLayout || state.itemCount <= 0) {
            return
        }
        for (index in 0 until childCount) {
            val child = getChildAt(index) ?: continue
            if (orientation == HORIZONTAL) {
                if (child.right < paddingLeft || child.left + paddingRight > width) {
                    removeAndRecycleView(child, recycler)
                }
            } else/* if (orientation == VERTICAL) */{
                if (child.bottom < paddingTop || child.top + paddingBottom > height) {
                    removeAndRecycleView(child, recycler)
                }
            }
        }
    }

    override fun isAutoMeasureEnabled(): Boolean = true
}