package zhupf.gadget.media

import android.util.Log

object Logger {

    interface Delegate {
        fun v(tag: String, msg: String)
        fun d(tag: String, msg: String)
        fun i(tag: String, msg: String)
        fun w(tag: String, msg: String, throwable: Throwable?)
        fun e(tag: String, msg: String, throwable: Throwable?)
    }

    var delegate: Delegate = object : Delegate {
        override fun v(tag: String, msg: String) {
            Log.v(tag, msg)
        }

        override fun d(tag: String, msg: String) {
            Log.d(tag, msg)
        }

        override fun i(tag: String, msg: String) {
            Log.i(tag, msg)
        }

        override fun w(tag: String, msg: String, throwable: Throwable?) {
            Log.w(tag, msg, throwable)
        }

        override fun e(tag: String, msg: String, throwable: Throwable?) {
            Log.e(tag, msg, throwable)
        }
    }
}

internal fun String.v(msg: String) {
    Logger.delegate.v(this, msg)
}

internal fun String.d(msg: String) {
    Logger.delegate.d(this, msg)
}

internal fun String.i(msg: String) {
    Logger.delegate.i(this, msg)
}

internal fun String.w(msg: String, throwable: Throwable? = null) {
    Logger.delegate.w(this, msg, throwable)
}

internal fun String.e(msg: String, throwable: Throwable? = null) {
    Logger.delegate.e(this, msg, throwable)
}