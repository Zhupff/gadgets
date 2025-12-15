package gadget.theme.mcu.color

import kotlin.math.pow

internal class ARGB private constructor(
    val value: Int,
    val a: Int,
    val r: Int,
    val g: Int,
    val b: Int,
) {

    companion object {

        fun linearized(component: Int): Double {
            val normalized = component.coerceIn(0, 255) / 255.0
            return if (normalized <= 0.040449936) {
                normalized / 12.92 * 100.0
            } else {
                ((normalized + 0.055) / 1.055).pow(2.4) * 100.0
            }
        }

        fun delinearized(component: Double): Int {
            val normalized = component.coerceIn(0.0, 100.0) / 100.0
            return Math.round((if (normalized <= 0.0031308) {
                normalized * 12.92
            } else {
                1.055 * normalized.pow(1.0 / 2.4) - 0.055
            } * 255.0)).toInt().coerceIn(0, 255)
        }

        fun delinearized2(component: Double): Double {
            val normalized = component / 100.0
            return if (normalized <= 0.0031308) {
                normalized * 12.92
            } else {
                1.055 * normalized.pow(1.0 / 2.4) - 0.055
            } * 255.0
        }

    }

    constructor(argb: Int) : this(
        argb, (argb shr 24) and 255, (argb shr 16) and 255, (argb shr 8) and 255, argb and 255,
    )

    constructor(a: Int, r: Int, g: Int, b: Int) : this(
        (a shl 24) or (r shl 16) or (g shl 8) or b, a, r, g, b,
    )

    override fun toString(): String = "#%02X%02X%02X%02X".format(a, r, g, b)
}