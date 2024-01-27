package zhupf.gadget.logger

import android.util.Log
import androidx.annotation.IntDef
import kotlin.reflect.KClass

object GLog {

    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @IntDef(Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR, Log.ASSERT)
    annotation class LevelRange

    @LevelRange var minLevel: Int = Log.VERBOSE

    @LevelRange var maxLevel: Int = Log.ERROR

    @JvmStatic
    fun v(tag: Any, info: Any?): Any = log(Log.VERBOSE, tag, info)

    @JvmStatic
    fun d(tag: Any, info: Any?): Any = log(Log.DEBUG, tag, info)

    @JvmStatic
    fun i(tag: Any, info: Any?): Any = log(Log.INFO, tag, info)

    @JvmStatic
    fun w(tag: Any, info: Any?): Any = log(Log.WARN, tag, info)

    @JvmStatic
    fun e(tag: Any, info: Any?): Any = log(Log.ERROR, tag, info)

    @JvmStatic
    fun s(tag: Any, deep: Int = 4, @LevelRange level: Int = Log.DEBUG): Any {
        val tagStr = tag.asTag
        val stack = Thread.currentThread().stackTrace
        for (i in deep until stack.size) {
            log(level, tagStr, stack[i])
        }
        return tag
    }

    private fun log(level: Int, tag: Any, info: Any?): Any {
        if (level in minLevel..maxLevel) {
            Log.println(level, tag.asTag, info.asInfo)
        }
        return tag
    }

    private val Any.asTag: String; get() = when (this) {
        is String -> this
        is KClass<*> -> java.simpleName
        is Class<*> -> simpleName
        else -> "${javaClass.simpleName}(${hashCode()})"
    }

    private val Any?.asInfo: String; get() = when (this) {
        is String -> this
        is Throwable -> Log.getStackTraceString(this)
        is StackTraceElement -> "${this.className}-${this.methodName} (${this.fileName}:${this.lineNumber})"
        else -> this.toString()
    }
}

fun Any.logV(any: Any?) = GLog.v(this, any)
fun Any.logD(any: Any?) = GLog.d(this, any)
fun Any.logI(any: Any?) = GLog.i(this, any)
fun Any.logW(any: Any?) = GLog.w(this, any)
fun Any.logE(any: Any?) = GLog.e(this, any)
fun Any.logS(deep: Int = 5, @GLog.LevelRange level: Int = Log.DEBUG) = GLog.s(this, deep, level)