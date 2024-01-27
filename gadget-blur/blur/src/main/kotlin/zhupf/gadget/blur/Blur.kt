package zhupf.gadget.blur

import android.graphics.Bitmap

interface Blur {

    fun sync(config: BlurConfig): Bitmap?

    fun async(config: BlurConfig, callback: AsyncCallback)

    fun release()

    interface AsyncCallback {
        fun callback(blurred: Bitmap?)
    }
}