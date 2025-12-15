package gadget.theme.mcu.palette

import gadget.theme.mcu.color.ARGB
import gadget.theme.mcu.color.HCT

internal class Palette private constructor(
    val hct: HCT,
    val hue: Double,
    val chroma: Double,
) {

    constructor(hct: HCT) : this(hct, hct.h, hct.c)

    constructor(hue: Double, chroma: Double) : this(HCT(HCT.solve(hue, chroma)), hue, chroma)

    private val cache: MutableMap<Int, Int> = HashMap()

    fun tone(tone: Int): Int = cache.getOrPut(tone) {
        if (tone == 99 && HCT.isYellow(hue)) {
            val argb1 = ARGB(tone(98))
            val argb2 = ARGB(tone(100))
            ARGB(0xFF, Math.round((argb1.r + argb2.r) / 2F), Math.round((argb1.g + argb2.g) / 2F), Math.round((argb1.b + argb2.b) / 2F)).value
        } else {
            HCT.solve(hue, chroma, tone.toDouble()).value
        }
    }
}