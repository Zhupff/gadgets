package gadget.basic

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object Filer {

    @JvmStatic
    fun save(inputStream: InputStream, outputFile: File) {
        inputStream.use { iStream ->
            FileOutputStream(outputFile).use { oStream ->
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



fun InputStream.saveTo(outputFile: File) {
    Filer.save(this, outputFile)
}

fun File.saveFrom(inputStream: InputStream) {
    Filer.save(inputStream, this)
}