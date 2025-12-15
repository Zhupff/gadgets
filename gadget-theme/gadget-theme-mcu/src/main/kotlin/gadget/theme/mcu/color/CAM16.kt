package gadget.theme.mcu.color

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.ln1p
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

internal class CAM16(
    val argb: ARGB,
) {

    val hue: Double
    val chroma: Double
    val lightness: Double
    val brightness: Double
    val colorfulness: Double
    val saturation: Double
    val stars: Triple<Double, Double, Double>

    init {
        val rL = ARGB.linearized(argb.r)
        val gL = ARGB.linearized(argb.g)
        val bL = ARGB.linearized(argb.b)
        val x = (0.41233895 * rL) + (0.35762064 * gL) + (0.18051042 * bL)
        val y = (0.2126     * rL) + (0.7152     * gL) + (0.0722     * bL)
        val z = (0.01932141 * rL) + (0.11916382 * gL) + (0.95034478 * bL)
        val rT = (0.401288  * x) + (0.650173 * y) + (-0.051461 * z)
        val gT = (-0.250268 * x) + (1.204414 * y) + (0.045854  * z)
        val bT = (-0.002079 * x) + (0.048952 * y) + (0.953127  * z)
        val rD = 1.02117770275752   * rT
        val gD = 0.9863077294280124 * gT
        val bD = 0.9339605082802299 * bT
        val rAF = (0.3884814537800353 * abs(rD) / 100.0).pow(0.42)
        val gAF = (0.3884814537800353 * abs(gD) / 100.0).pow(0.42)
        val bAF = (0.3884814537800353 * abs(bD) / 100.0).pow(0.42)
        val rA = rD.sign * 400.0 * rAF / (rAF + 27.13)
        val gA = gD.sign * 400.0 * gAF / (gAF + 27.13)
        val bA = bD.sign * 400.0 * bAF / (bAF + 27.13)
        val r2g = (11.0 * rA + -12.0 * gA + bA) / 11.0
        val y2b = (rA + gA - 2.0 * bA) / 9.0
        val degree = Math.toDegrees(atan2(y2b, r2g))
        this.hue = if (degree < 0.0) {
            degree + 360.0
        } else if (degree >= 360.0) {
            degree - 360.0
        } else {
            degree
        }
        val eHue = 0.25 * (cos(Math.toRadians(if (this.hue < 20.14) this.hue + 360.0 else this.hue) + 2.0) + 3.8)
        val alpha = (1.64 - 0.29.pow(0.18418651851244416)).pow(0.73) * (50000.0 / 13.0 * eHue * 1.0169191804458755 * hypot(r2g, y2b) / ((20.0 * rA + 20.0 * gA + 21.0 * bA) / 20.0 + 0.305)).pow(0.9)
        this.lightness = 100.0 * ((40.0 * rA + 20.0 * gA + bA) / 20.0 * 1.0169191804458755 / 29.980997194447333).pow(0.69 * 1.909169568483652)
        this.brightness = 4.0 / 0.69 * sqrt(this.lightness / 100.0) * (29.980997194447333 + 4.0) * 0.7894826179304937
        this.chroma = alpha * sqrt(this.lightness / 100.0)
        this.colorfulness = this.chroma * 0.7894826179304937
        this.saturation = 50.0 * sqrt((alpha * 0.69) / (29.980997194447333 + 4.0))
        this.stars = Triple(
            (1.0 + 100.0 * 0.007) * lightness / (1.0 + 0.007 * lightness),
            1.0 / 0.0228 * ln1p(0.0228 * colorfulness) * cos(Math.toRadians(this.hue)),
            1.0 / 0.0228 * ln1p(0.0228 * colorfulness) * sin(Math.toRadians(this.hue)),
        )
    }
}