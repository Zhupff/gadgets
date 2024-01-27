package zhupf.gadget.toast

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes

object Toaster {

    internal lateinit var applicationContext: Context; private set

    private lateinit var singleToast: Toast

    fun init(context: Context) {
        if (!Looper.getMainLooper().isCurrentThread) {
            throw IllegalStateException("Toaster init should be invoked in main thread.")
        }
        applicationContext = context
        singleToast = Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT)
    }

    private val handler: Handler = Handler(Looper.getMainLooper())

    private class ToastRunnable(
        val single: Boolean,
        val content: CharSequence? = null,
        @StringRes val id: Int? = null,
        val duration: Int
    ) : Runnable {
        override fun run() {
            if (single) {
                if (content != null) {
                    singleToast.setText(content)
                } else if (id != null) {
                    singleToast.setText(id)
                } else {
                    throw IllegalArgumentException("both content and id are null.")
                }
                singleToast.duration = duration
                singleToast.show()
            } else {
                if (content != null) {
                    Toast.makeText(applicationContext, content, duration).show()
                } else if (id != null) {
                    Toast.makeText(applicationContext, id, duration).show()
                } else {
                    throw IllegalArgumentException("both content and id are null.")
                }
            }
        }
    }

    fun singleToastS(content: CharSequence) {
        toast(true, content, null, Toast.LENGTH_SHORT)
    }

    fun singleToastL(content: CharSequence) {
        toast(true, content, null, Toast.LENGTH_LONG)
    }

    fun singleToastS(@StringRes id: Int) {
        toast(true, null, id, Toast.LENGTH_SHORT)
    }

    fun singleToastL(@StringRes id: Int) {
        toast(true, null, id, Toast.LENGTH_LONG)
    }

    fun toastS(content: CharSequence) {
        toast(false, content, null, Toast.LENGTH_SHORT)
    }

    fun toastL(content: CharSequence) {
        toast(false, content, null, Toast.LENGTH_LONG)
    }

    fun toastS(@StringRes id: Int) {
        toast(false, null, id, Toast.LENGTH_SHORT)
    }

    fun toastL(@StringRes id: Int) {
        toast(false, null, id, Toast.LENGTH_LONG)
    }

    private fun toast(
        single: Boolean,
        charSequence: CharSequence?,
        @StringRes id: Int?,
        duration: Int
    ) {
        if (Looper.getMainLooper().isCurrentThread) {
            ToastRunnable(single, charSequence, id, duration).run()
        } else {
            handler.post(ToastRunnable(single, charSequence, id, duration))
        }
    }
}

fun CharSequence.singleToastS() {
    Toaster.singleToastS(this)
}

fun CharSequence.singleToastL() {
    Toaster.singleToastL(this)
}

fun Int.singleToastS() {
    Toaster.singleToastS(this)
}

fun Int.singleToastL() {
    Toaster.singleToastL(this)
}

fun CharSequence.toastS() {
    Toaster.toastS(this)
}

fun CharSequence.toastL() {
    Toaster.toastL(this)
}

fun Int.toastS() {
    Toaster.toastS(this)
}

fun Int.toastL() {
    Toaster.toastL(this)
}