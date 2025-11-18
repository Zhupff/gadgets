package gadget.widget

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration

class GestureDetectView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val IDLE: Int = 0
        const val HORIZONTALLY: Int = 1
        const val VERTICALLY: Int = 2

        const val VELOCITY_UNIT = 1000

        private const val SINGLE_TOUCH = 1
        private const val SINGLE_PRESS = 2
        private const val DOUBLE_TOUCH = 3
        private const val DOUBLE_PRESS = 4
    }

    var gestureDetectListener: GestureDetectListener? = null

    private var touchX = Float.MIN_VALUE
    private var touchY = Float.MIN_VALUE
    private var touchType = IDLE
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    private val touchSlopSquare = touchSlop * touchSlop
    private var isScrolling = false
    private var scrollDirection = 0
    private var velocityTracker: VelocityTracker? = null

    private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
        private var doubleTouchTs = 0L
        private val longPressTimeout = ViewConfiguration.getLongPressTimeout()
        override fun onDown(e: MotionEvent): Boolean {
            touchX = e.x
            touchY = e.y
            if (touchType == IDLE) {
                touchType = SINGLE_TOUCH
                gestureDetectListener?.onSingleTouch(e.x, e.y)
            }
            return super.onDown(e)
        }
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            gestureDetectListener?.onSingleTap(e.x, e.y)
            return super.onSingleTapConfirmed(e)
        }
        override fun onDoubleTap(e: MotionEvent): Boolean {
            parent.requestDisallowInterceptTouchEvent(true)
            touchX = e.x
            touchY = e.y
            touchType = DOUBLE_TOUCH
            doubleTouchTs = SystemClock.elapsedRealtime()
            gestureDetectListener?.onDoubleTouch(e.x, e.y)
            return super.onDoubleTap(e)
        }
        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            when (e.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> {
                    if ((e.x - touchX) * (e.x - touchX) + (e.y - touchY) * (e.y - touchY) < touchSlopSquare &&
                        SystemClock.elapsedRealtime() - doubleTouchTs < longPressTimeout) {
                        gestureDetectListener?.onDoubleTap(e.x, e.y)
                    }
                    doubleTouchTs = 0L
                }
                MotionEvent.ACTION_CANCEL -> {
                    doubleTouchTs = 0L
                }
            }
            return super.onDoubleTapEvent(e)
        }
        override fun onLongPress(e: MotionEvent) {
            parent.requestDisallowInterceptTouchEvent(true)
            touchX = e.x
            touchY = e.y
            if (touchType == SINGLE_TOUCH && !isScrolling) {
                touchType = SINGLE_PRESS
                gestureDetectListener?.onSinglePress(e.x, e.y)
            } else if (touchType == DOUBLE_TOUCH && !isScrolling) {
                doubleTouchTs = 0L
                touchType = DOUBLE_PRESS
                gestureDetectListener?.onDoublePress(e.x, e.y)
            }
            super.onLongPress(e)
        }
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            parent.requestDisallowInterceptTouchEvent(true)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - touchX
                val dy = event.y - touchY
                if (scrollDirection == IDLE) {
                    if (dx > touchSlop || dx < -touchSlop) {
                        scrollDirection = HORIZONTALLY
                    } else if (dy > touchSlop || dy < -touchSlop) {
                        scrollDirection = VERTICALLY
                    }
                    isScrolling = scrollDirection != IDLE
                }
                if (scrollDirection != IDLE) {
                    val vt = velocityTracker ?: VelocityTracker.obtain().also { velocityTracker = it }
                    vt.addMovement(event)
                    vt.computeCurrentVelocity(VELOCITY_UNIT)
                    val distance = if (scrollDirection == HORIZONTALLY) dx else if (scrollDirection == VERTICALLY) dy else 0F
                    val velocity = if (scrollDirection == HORIZONTALLY) vt.xVelocity else if (scrollDirection == VERTICALLY) vt.yVelocity else 0F
                    when (touchType) {
                        SINGLE_TOUCH -> gestureDetectListener?.onSingleTouchScroll(scrollDirection, distance, velocity)
                        SINGLE_PRESS -> gestureDetectListener?.onSinglePressScroll(scrollDirection, distance, velocity)
                        DOUBLE_TOUCH -> gestureDetectListener?.onDoubleTouchScroll(scrollDirection, distance, velocity)
                        DOUBLE_PRESS -> gestureDetectListener?.onDoublePressScroll(scrollDirection, distance, velocity)
                    }
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                val shouldCallOnTouchLeave = isScrolling || touchType == SINGLE_PRESS || touchType == DOUBLE_PRESS
                touchType = IDLE
                scrollDirection = IDLE
                isScrolling = false
                velocityTracker?.recycle()
                velocityTracker = null
                parent.requestDisallowInterceptTouchEvent(false)
                if (shouldCallOnTouchLeave) {
                    gestureDetectListener?.onTouchLeave(event.x, event.y)
                }
            }
        }
        return true
    }

    interface GestureDetectListener {
        fun onSingleTouch(tx: Float, ty: Float) {}
        fun onSingleTap(tx: Float, ty: Float) {}
        fun onDoubleTouch(tx: Float, ty: Float) {}
        fun onDoubleTap(tx: Float, ty: Float) {}
        fun onSinglePress(tx: Float, ty: Float) {}
        fun onDoublePress(tx: Float, ty: Float) {}
        fun onSingleTouchScroll(direction: Int, distance: Float, velocity: Float) {}
        fun onSinglePressScroll(direction: Int, distance: Float, velocity: Float) {}
        fun onDoubleTouchScroll(direction: Int, distance: Float, velocity: Float) {}
        fun onDoublePressScroll(direction: Int, distance: Float, velocity: Float) {}
        fun onTouchLeave(tx: Float, ty: Float) {}
    }
}