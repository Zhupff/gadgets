package zhupff.gadgets.basic

import java.io.InputStream
import java.security.MessageDigest

object MD5 {

    private val DIGEST: MessageDigest; get() = MessageDigest.getInstance("MD5")

    private val HEX_CHARS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    fun digestBytes(bytes: ByteArray): ByteArray = DIGEST.digest(bytes)

    fun digestBytes(iStream: InputStream): ByteArray {
        val digest = DIGEST
        val buffer = ByteArray(1024 * 4)
        var length = 0
        while (iStream.read(buffer).also { length = it } != -1) {
            digest.update(buffer, 0, length)
        }
        return digest.digest()
    }

    fun digestChars(bytes: ByteArray): CharArray = encodeHex(digestBytes(bytes))

    fun digestChars(iStream: InputStream): CharArray = encodeHex(digestBytes(iStream))

    fun digestHex(bytes: ByteArray): String = String(digestChars(bytes))

    fun digestHex(iStream: InputStream): String = String(digestChars(iStream))


    private fun encodeHex(bytes: ByteArray): CharArray = CharArray(32) { i ->
        if (i and 1 == 0) {
            HEX_CHARS[bytes[i / 2].toInt() ushr 4 and 15]
        } else {
            HEX_CHARS[bytes[i / 2].toInt() and 15]
        }
    }
}