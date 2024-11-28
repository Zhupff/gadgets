package zhupff.gadgets.basic

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object Filer {

    fun save(inputStream: InputStream, output: File) {
        inputStream.use { iStream ->
            FileOutputStream(output).use { oStream ->
                val buffer = ByteArray(1024 * 4)
                while (true) {
                    val length = iStream.read(buffer)
                    if (length != -1) {
                        oStream.write(buffer, 0, length)
                    } else {
                        oStream.flush()
                        break
                    }
                }
            }
        }
    }
}

fun <F : File> F.mkdirsIfNotExists(): F = apply {
    if (!exists()) mkdirs()
}

fun <F : File> F.createIfNotExists(): F = apply {
    if (!exists()) {
        val p = parentFile?.mkdirsIfNotExists()
        createNewFile()
    }
}