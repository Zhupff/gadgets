package zhupff.gadgets.qrcode.common

import kotlin.math.min

class BitSource(
    private val bytes: ByteArray,
) {

    private var byteOffset = 0

    private var bitOffset = 0

    fun readBits(numBits: Int): Int {
        assert(numBits in 1..32 && numBits <= available())
        var numBits = numBits
        var result = 0
        if (bitOffset > 0) {
            val bitsLeft = 8 - bitOffset
            val toRead = min(numBits, bitsLeft)
            val bitsNotToRead = bitsLeft - toRead
            val mask = (0xFF shr (8 - toRead)) shl bitsNotToRead
            result = (bytes[byteOffset].toInt() and mask) shr bitsNotToRead
            numBits -= toRead
            bitOffset += toRead
            if (bitOffset == 8) {
                bitOffset = 0
                byteOffset += 1
            }
        }
        if (numBits > 0) {
            while (numBits >= 8) {
                result = (result shl 8) or (bytes[byteOffset].toInt() and 0xFF)
                byteOffset += 1
                numBits -= 8
            }
            if (numBits > 0) {
                val bitsNotToRead = 8 - numBits
                val mask = (0xFF shr bitsNotToRead) shl bitsNotToRead
                result = (result shl numBits) or ((bytes[byteOffset].toInt() and mask) shr bitsNotToRead)
                bitOffset += numBits
            }
        }
        return result
    }

    fun available(): Int = 8 * (bytes.size - byteOffset) - bitOffset
}