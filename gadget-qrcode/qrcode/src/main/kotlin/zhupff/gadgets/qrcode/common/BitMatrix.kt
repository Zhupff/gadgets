package zhupff.gadgets.qrcode.common

class BitMatrix(
    val width: Int,
    val height: Int,
) {

    init {
        assert(width >= 1 && height >= 1)
    }

    private val rowSize = (width + 31) / 32

    private val bits = IntArray(rowSize * height)

    fun get(x: Int, y: Int): Boolean {
        val offset = y * rowSize + x / 32
        return ((bits[offset] ushr (x and 0x1F)) and 1) != 0
    }

    fun set(x: Int, y: Int) {
        val offset = y * rowSize + x / 32
        bits[offset] = bits[offset] or (1 shl (x and 0x1F))
    }

    fun flip(x: Int, y: Int) {
        val offset = y * rowSize + x / 32
        bits[offset] = bits[offset] xor (1 shl (x and 0x1F))
    }

    fun region(left: Int, top: Int, width: Int, height: Int) {
        assert(left >= 0 && top >= 0 && width >= 1 && height >= 1)
        val right = left + width
        val bottom = top + height
        assert(bottom <= this.height && right <= this.width)
        for (y in top until bottom) {
            for (x in left until right) {
                val offset = y * rowSize + x / 32
                bits[offset] = bits[offset] or (1 shl (x and 0x1F))
            }
        }
    }
}