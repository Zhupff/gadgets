package gadget.widget.blur

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class StackBlur : Blur {

    companion object : Blur {
        override fun blur(pixels: IntArray, width: Int, height: Int, radius: Int) {
            assert(width > 0 && height > 0 && pixels.size == width * height)
            if (radius < 1) return
            val division = radius + radius + 1
            val div = ((division + 1 shr 1) * (division + 1 shr 1)).let { size ->
                IntArray(256 * size) { index -> index / size }
            }
            val rgbSum = IntArray(9) // rSum, gSum, bSum, rInSum, gInSum, bInSum, rOutSum, gOutSum, bOutSum
            val stack = Array(division) { IntArray(3) }
            val index = IntArray(3) // temporary variable of unknown meaning

            for (y in 0 until height) {
                rgbSum.fill(0)
                for (i in -radius..radius) {
                    val p = pixels[index[0] + min(width - 1, max(i, 0))]
                    val s = stack[i + radius]
                    s[0] = p.red; s[1] = p.green; s[2] = p.blue
                    val r = radius + 1 - abs(i)
                    rgbSum[0] += s[0] * r; rgbSum[1] += s[1] * r; rgbSum[2] += s[2] * r
                    if (i > 0) {
                        rgbSum[3] += s[0]; rgbSum[4] += s[1]; rgbSum[5] += s[2]
                    } else {
                        rgbSum[6] += s[0]; rgbSum[7] += s[1]; rgbSum[8] += s[2]
                    }
                }
                var pointer = radius
                for (x in 0 until width) {
                    pixels[index[0]] = (0xFF000000.toInt() and pixels[index[0]]) or (div[rgbSum[0]] shl 16) or (div[rgbSum[1]] shl 8) or div[rgbSum[2]]
                    rgbSum[0] -= rgbSum[6]; rgbSum[1] -= rgbSum[7]; rgbSum[2] -= rgbSum[8]
                    var s = stack[(pointer - radius + division) % division]
                    rgbSum[6] -= s[0]; rgbSum[7] -= s[1]; rgbSum[8] -= s[2]
                    val p = pixels[index[1] + min(x + radius + 1, width - 1)]
                    s[0] = p.red; s[1] = p.green; s[2] = p.blue
                    rgbSum[3] += s[0]; rgbSum[4] += s[1]; rgbSum[5] += s[2]
                    rgbSum[0] += rgbSum[3]; rgbSum[1] += rgbSum[4]; rgbSum[2] += rgbSum[5]
                    pointer = (pointer + 1) % division
                    s = stack[pointer]
                    rgbSum[3] -= s[0]; rgbSum[4] -= s[1]; rgbSum[5] -= s[2]
                    rgbSum[6] += s[0]; rgbSum[7] += s[1]; rgbSum[8] += s[2]
                    index[0]++
                }
                index[1] += width
            }

            for (x in 0 until width) {
                rgbSum.fill(0)
                index[2] = -radius * width
                for (i in -radius..radius) {
                    index[0] = max(0, index[2]) + x
                    val s = stack[i + radius]
                    s[0] = pixels[index[0]].red; s[1] = pixels[index[0]].green; s[2] = pixels[index[0]].blue
                    val r = radius + 1 - abs(i)
                    rgbSum[0] += s[0] * r; rgbSum[1] += s[1] * r; rgbSum[2] += s[2] * r
                    if (i > 0) {
                        rgbSum[3] += s[0]; rgbSum[4] += s[1]; rgbSum[5] += s[2]
                    } else {
                        rgbSum[6] += s[0]; rgbSum[7] += s[1]; rgbSum[8] += s[2]
                    }
                    if (i < height - 1) index[2] += width
                }
                index[0] = x
                var pointer = radius
                for (y in 0 until height) {
                    pixels[index[0]] = (0xFF000000.toInt() and pixels[index[0]]) or (div[rgbSum[0]] shl 16) or (div[rgbSum[1]] shl 8) or div[rgbSum[2]]
                    rgbSum[0] -= rgbSum[6]; rgbSum[1] -= rgbSum[7]; rgbSum[2] -= rgbSum[8]
                    var s = stack[(pointer - radius + division) % division]
                    rgbSum[6] -= s[0]; rgbSum[7] -= s[1]; rgbSum[8] -= s[2]
                    val p = x + min(y + radius + 1, height - 1) * width
                    s[0] = pixels[p].red; s[1] = pixels[p].green; s[2] = pixels[p].blue
                    rgbSum[3] += s[0]; rgbSum[4] += s[1]; rgbSum[5] += s[2]
                    rgbSum[0] += rgbSum[3]; rgbSum[1] += rgbSum[4]; rgbSum[2] += rgbSum[5]
                    pointer = (pointer + 1) % division
                    s = stack[pointer]
                    rgbSum[3] -= s[0]; rgbSum[4] -= s[1]; rgbSum[5] -= s[2]
                    rgbSum[6] += s[0]; rgbSum[7] += s[1]; rgbSum[8] += s[2]
                    index[0] += width
                }
            }
        }
    }

    override fun blur(pixels: IntArray, width: Int, height: Int, radius: Int) {
        StackBlur.blur(pixels, width, height, radius)
    }
}