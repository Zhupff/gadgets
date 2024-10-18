package zhupff.gadgets.basic

import java.io.PrintWriter
import java.io.StringWriter

class PrintFormat {
    companion object {
        const val ESC = '\u001B'
        const val RESET = "$ESC[0m"
        const val LEVEL_V = "$ESC[37m"
        const val LEVEL_D = "$ESC[30m"
        const val LEVEL_I = "$ESC[34m"
        const val LEVEL_W = "$ESC[33m"
        const val LEVEL_E = "$ESC[31m"
    }

    private val sb: StringBuilder = StringBuilder()



    fun v(any: Any): String = "${LEVEL_V}${any.toMsg}${RESET}"

    fun d(any: Any): String = "${LEVEL_D}${any.toMsg}${RESET}"

    fun i(any: Any): String = "${LEVEL_I}${any.toMsg}${RESET}"

    fun w(any: Any): String = "${LEVEL_W}${any.toMsg}${RESET}"

    fun e(any: Any): String = "${LEVEL_E}${any.toMsg}${RESET}"



    fun append(any: Any) {
        sb.append(any)
    }

    fun appendV(any: Any) {
        sb.append(v(any))
    }

    fun appendD(any: Any) {
        sb.append(d(any))
    }

    fun appendI(any: Any) {
        sb.append(i(any))
    }

    fun appendW(any: Any) {
        sb.append(w(any))
    }

    fun appendE(any: Any) {
        sb.append(e(any))
    }

    fun appendLn() {
        sb.appendLine()
    }

    fun appendLn(any: Any) {
        sb.appendLine(any.toMsg)
    }

    fun appendLnV(any: Any) {
        sb.appendLine(v(any))
    }

    fun appendLnD(any: Any) {
        sb.appendLine(d(any))
    }

    fun appendLnI(any: Any) {
        sb.appendLine(i(any))
    }

    fun appendLnW(any: Any) {
        sb.appendLine(w(any))
    }

    fun appendLnE(any: Any) {
        sb.appendLine(e(any))
    }



    override fun toString(): String = sb.toString()

    private val Any.toMsg: String; get() = when (this) {
        is String -> this
        is Throwable -> {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            printStackTrace(pw)
            pw.flush()
            sw.toString()
        }
        is StackTraceElement -> "${this.className}-${this.methodName} (${this.fileName}:${this.lineNumber})"
        else -> this.toString()
    }
}


fun printF(
    block: PrintFormat.() -> Unit,
) {
    val printFormat = PrintFormat()
    printFormat.block()
    print(printFormat.toString())
}

fun printlnF(
    block: PrintFormat.() -> Unit,
) {
    val printFormat = PrintFormat()
    printFormat.block()
    println(printFormat.toString())
}