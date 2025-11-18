package gadget.widget.attribute

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams

interface ResizeStrategy {

    companion object Default : ResizeStrategy {
        override fun resize(view: View, contentWidth: Int, contentHeight: Int, ratio: Float) {
            if (contentWidth <= 0 || contentHeight <= 0) {
                return
            }
            if (ratio < 0F) {
                throw IllegalArgumentException("ratio should not be less than 0!")
            }
            val lp = view.layoutParams ?: ViewGroup.LayoutParams(contentWidth, contentHeight)
            if (ratio == 0F) {
                // 如果比例为0，代表让view填充满容器
                lp.width = LayoutParams.MATCH_PARENT
                lp.height = LayoutParams.MATCH_PARENT
            } else {
                val contentRatio = contentWidth.toFloat() / contentHeight.toFloat()
                if (contentRatio < ratio) {
                    lp.width = contentWidth
                    lp.height = (contentWidth / ratio).toInt()
                } else if (contentRatio > ratio) {
                    lp.height = contentHeight
                    lp.width = (contentHeight * ratio).toInt()
                } else {
                    lp.width = contentWidth
                    lp.height = contentHeight
                }
            }
            view.layoutParams = lp
        }
    }

    /**
     * 重设尺寸
     * @param view 要重设尺寸的View
     * @param contentWidth 容纳[view]的容器宽度
     * @param contentHeight 容纳[view]的容器高度
     * @param ratio view需要保持的w/h比例
     */
    fun resize(view: View, contentWidth: Int, contentHeight: Int, ratio: Float)
}