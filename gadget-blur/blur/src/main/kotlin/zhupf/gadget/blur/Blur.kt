package zhupf.gadget.blur

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

interface Blur {

    companion object {

        private val EXECUTOR = Executors.newFixedThreadPool(
            (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1),
            object : ThreadFactory {
                val group = System.getSecurityManager()?.threadGroup ?: Thread.currentThread().threadGroup
                val id = AtomicInteger(0)
                override fun newThread(runnable: Runnable?): Thread = Thread(group, runnable, "blur-thread-${id.incrementAndGet()}")
            }
        )

        fun <V> submit(callable: Callable<V>): Future<V> = EXECUTOR.submit(callable)
    }

    fun blur(pixels: IntArray, width: Int, height: Int, radius: Int)
}