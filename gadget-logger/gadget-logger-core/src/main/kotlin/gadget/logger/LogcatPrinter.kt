package gadget.logger

import android.util.Log

open class LogcatPrinter : Logger.Printer {
    override fun print(priority: Int, tag: String, content: String, throwable: Throwable?, timestamp: Long) {
        when (priority) {
            Log.VERBOSE -> Log.v(tag, content, throwable)
            Log.DEBUG   -> Log.d(tag, content, throwable)
            Log.INFO    -> Log.i(tag, content, throwable)
            Log.WARN    -> Log.w(tag, content, throwable)
            Log.ERROR   -> Log.e(tag, content, throwable)
        }
    }
}