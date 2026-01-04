import com.google.mcu.quantize.QuantizerCelebi
import com.google.mcu.score.Score
import java.io.File

fun main() {
    testQuantize()
}

private fun testQuantize() {
    // [#FF625141, #FFC4B8A7]
    val pixelsJson = File("pixels.json").readText(Charsets.UTF_8).replace("[", "").replace("]", "")
    val pixels = pixelsJson.split(',').map { it.toInt() }.toIntArray()
    val quantizedResult = QuantizerCelebi.quantize(pixels, 128)
    println("quantized-result: ${quantizedResult}")
    val scoredResult = Score.score(quantizedResult)
    println("scored-result: ${scoredResult.map { "#%02X%02X%02X%02X".format((it shr 24) and 255, (it shr 16) and 255, (it shr 8) and 255, it and 255) }}")
}
