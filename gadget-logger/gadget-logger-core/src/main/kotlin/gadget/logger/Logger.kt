package gadget.logger

import android.util.Log
import androidx.annotation.IntDef
import java.util.function.Supplier

class Logger @JvmOverloads constructor(
    private val prefix: String?,
    private val printers: List<Printer>,
    private val interceptor: Interceptor = object : Interceptor {
        override fun intercept(priority: Int, tag: String): Boolean = false
    },
) {

    @IntDef(Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Level

    /**
     * 拦截器
     */
    interface Interceptor {
        /**
         * 是否拦截
         * @param priority 日志等级
         * @param tag 日志标签
         * @return true=不做后续处理，false=正常执行后续流程
         */
        fun intercept(@Level priority: Int, tag: String): Boolean
    }

    interface Printer {
        /**
         * @param priority 日志的等级
         * @param tag 日志标签
         * @param content 日志内容
         * @param throwable 错误信息
         * @param timestamp 日志发生时的时间戳
         */
        fun print(@Level priority: Int, tag: String, content: String, throwable: Throwable?, timestamp: Long)
    }

    fun v(tag: String, content: Supplier<String>) {
        print(Log.VERBOSE, tag, content, null)
    }

    fun d(tag: String, content: Supplier<String>) {
        print(Log.DEBUG, tag, content, null)
    }

    fun i(tag: String, content: Supplier<String>) {
        print(Log.INFO, tag, content, null)
    }

    fun w(tag: String, throwable: Throwable?, content: Supplier<String>) {
        print(Log.WARN, tag, content, throwable)
    }

    fun e(tag: String, throwable: Throwable, content: Supplier<String>) {
        print(Log.ERROR, tag, content, throwable)
    }

    fun print(@Level priority: Int, tag: String, content: Supplier<String>, throwable: Throwable?) {
        if (interceptor.intercept(priority, tag)) {
            return
        }
        val finalTag = if (prefix.isNullOrBlank()) tag else prefix + tag
        val finalContent = content.get()
        val timestamp = System.currentTimeMillis()
        printers.forEach { it.print(priority, finalTag, finalContent, throwable, timestamp) }
    }
}