package gadget.logger

import android.os.Handler
import android.os.HandlerThread
import android.system.Os
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.getOrSet

open class FilePrinter @JvmOverloads constructor(
    protected val ioThreadName: String,
    protected val fileProvider: FileProvider = object : FileProvider {
        override fun provide(): File = applicationContext.filesDir.resolve("_LOG_").also(File::mkdirs)
    },
    protected val timeFormater: TimeFormater = object : TimeFormater {
        private val date = Date()
        private val formater = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS")
        override fun format(timestamp: Long): String {
            date.time = timestamp
            return formater.format(date)
        }
    },
    protected val errorCatcher: ErrorCatcher = object : ErrorCatcher {
        override fun onError(message: String, throwable: Throwable) {
            Log.w("FilePrinter", message, throwable)
        }
    },
) : Logger.Printer {

    companion object {
        protected val PROCESS_ID: Int = android.os.Process.myPid()
        /** 线程ID */
        protected val THREAD_ID: ThreadLocal<Int> = ThreadLocal()
        /** 满x条为一阶段，触发flush */
        protected const val PERIOD_COUNT: Long = 100L
        /** 满x条为一片段，切换写入的文件 */
        protected const val SESSION_COUNT: Long = 100_000L
        /** 线程计数，防止出现同名线程 */
        protected val IO_THREAD_COUNTER = AtomicInteger(0)
    }

    /** 日志条数总数统计 */
    protected val totalCounter = AtomicLong(0)
    /** 日志条数阶段统计 */
    protected val periodCounter = AtomicLong(0)
    /** 日志条数片段统计 */
    protected val sessionCounter = AtomicLong(0)
    protected var fileLock = Any()
    @Volatile protected var fileWriter: PrintWriter? = null
    /** 是否需要写入文件 */
    @Volatile protected var flushFlag = false
    /** 是否已经标记为释放状态 */
    @Volatile protected var releaseFlag = false
    /** 写文件的线程 */
    protected val ioThread = HandlerThread(ioThreadName + IO_THREAD_COUNTER.incrementAndGet()).also(HandlerThread::start)
    /** 写文件的线程 */
    protected val ioHandler = Handler(ioThread.looper)


    override fun print(priority: Int, tag: String, content: String, throwable: Throwable?, timestamp: Long) {
        val totalCount = totalCounter.incrementAndGet()
        ioHandler.post {
            synchronized(fileLock) {
                val pw: PrintWriter? = fileWriter ?: try {
                    val file = fileProvider.provide().also(File::createNewFile)
                    PrintWriter(BufferedWriter(FileWriter(file, true), 1024))
                } catch (throwable: Throwable) {
                    errorCatcher.onError("FileWriter exception", throwable)
                    null
                }
                if (pw != null) {
                    fileWriter = pw
                    val finalContent = if (throwable == null) {
                        content
                    } else {
                        StringBuilder().appendLine(content).appendLine(Log.getStackTraceString(throwable)).toString()
                    }
                    pw.println("%s %5s-%-5s %-32s: %s".format(timeFormater.format(timestamp), PROCESS_ID, THREAD_ID.getOrSet { Os.gettid() }, tag, finalContent))
                    val periodCount = periodCounter.incrementAndGet()
                    val sessionCount = sessionCounter.incrementAndGet()
                    if (totalCount >= totalCounter.get() ||
                        periodCount >= PERIOD_COUNT ||
                        sessionCount >= SESSION_COUNT ||
                        flushFlag || releaseFlag) {
                        flush()
                    }
                }
            }
        }
    }

    protected open fun flush() {
        try {
            synchronized(fileLock) {
                val pw = fileWriter
                if (pw != null) {
                    pw.checkError()
                    periodCounter.set(0)
                    if (sessionCounter.get() > SESSION_COUNT) {
                        sessionCounter.set(0)
                        fileWriter = null
                        pw.close()
                    }
                }
            }
        } catch (throwable: Throwable) {
            errorCatcher.onError("Flush exception", throwable)
        } finally {
            flushFlag = false
        }
    }

    open fun release() {
        releaseFlag = true
        ioThread.quitSafely()
    }

    interface FileProvider {
        fun provide(): File
    }

    interface TimeFormater {
        fun format(timestamp: Long): String
    }

    interface ErrorCatcher {
        fun onError(message: String, throwable: Throwable)
    }
}