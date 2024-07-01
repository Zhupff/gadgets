package zhupff.gadgets.qrcode.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.google.zxing.ResultPoint

class QrCodePointView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var points: List<ResultPoint> = emptyList()
        set(value) {
            field = value
            postInvalidate()
        }

    private val pt = Paint().also {
        it.color = Color.RED
    }

    private val rt = Rect()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        points.forEach { p ->
            rt.set((p.x - 5).toInt(), (p.y - 5).toInt(), (p.x + 5).toInt(), (p.y + 5).toInt())
            canvas.drawRect(rt, pt)
        }
    }
}