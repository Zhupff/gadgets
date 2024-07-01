package zhupff.gadgets.qrcode.common

import kotlin.math.ceil

class BitArray {
    companion object {
        private val EMPTY = IntArray(0)
        private const val FACTOR = 0.75F
    }

    private var bits: IntArray = EMPTY

    var size: Int = 0; private set

    fun getSizeInBytes() = (size + 7) / 8

    fun get(i: Int) = (bits[i / 32] and (1 shl (i and 0x1F))) != 0

    fun appendBit(bit: Boolean) {
        ensureCapacity(size + 1)
        if (bit) {
            val index = size / 32
            bits[index] = bits[index] or (1 shl (size and 0x1F))
        }
        size += 1
    }

    fun appendBits(value: Int, numBits: Int) {
        assert(numBits in 0..32)
        var nextSize = size
        ensureCapacity(nextSize + numBits)
        for (numBitsLeft in numBits - 1 downTo  0) {
            if ((value and (1 shl numBitsLeft)) != 0) {
                val index = nextSize / 32
                bits[index] = bits[index] or (1 shl (nextSize and 0x1F))
            }
            nextSize += 1
        }
        size = nextSize
    }

    fun appendBitArray(other: BitArray) {
        val otherSize = other.size
        ensureCapacity(size + otherSize)
        for (i in 0 until otherSize) {
            appendBit(other.get(i))
        }
    }

    fun xor(other: BitArray) {
        assert(size == other.size)
        for (i in bits.indices) {
            bits[i] = bits[i] xor other.bits[i]
        }
    }

    fun toBytes(bitOffset: Int, array: ByteArray, offset: Int, numBytes: Int) {
        var bitOffset = bitOffset
        for (i in 0 until numBytes) {
            var theByte = 0
            for (j in 0 until 8) {
                if (get(bitOffset)) {
                    theByte = theByte or (1 shl (7 - j))
                }
                bitOffset += 1
            }
            array[offset + i] = theByte.toByte()
        }
    }

    private fun ensureCapacity(newSize: Int) {
        if (newSize > bits.size * 32) {
            this.bits = bits.copyInto(IntArray((ceil(newSize / FACTOR).toInt() + 31) / 32), 0, 0, bits.size)
        }
    }
}