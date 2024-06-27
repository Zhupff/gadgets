package zhupff.gadgets.theme

import android.graphics.Bitmap
import com.google.mdc.dynamiccolor.DynamicScheme
import com.google.mdc.dynamiccolor.Variant
import com.google.mdc.hct.Hct
import com.google.mdc.quantize.QuantizerCelebi
import com.google.mdc.scheme.SchemeContent
import com.google.mdc.scheme.SchemeExpressive
import com.google.mdc.scheme.SchemeFidelity
import com.google.mdc.scheme.SchemeFruitSalad
import com.google.mdc.scheme.SchemeMonochrome
import com.google.mdc.scheme.SchemeNeutral
import com.google.mdc.scheme.SchemeRainbow
import com.google.mdc.scheme.SchemeTonalSpot
import com.google.mdc.scheme.SchemeVibrant
import com.google.mdc.score.Score

object ThemeUtil {

    fun extractSeedColors(bitmap: Bitmap, callback: (List<Int>) -> Unit) {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val result = QuantizerCelebi.quantize(pixels, 3)
        val seedColors = Score.score(result)
        callback(seedColors)
    }

    fun makeScheme(seedColor: Int, isDark: Boolean, variant: Variant, callback: (DynamicScheme) -> Unit) {
        val ds = when (variant) {
            Variant.MONOCHROME -> SchemeMonochrome(Hct.fromInt(seedColor), isDark, 0.0)
            Variant.NEUTRAL -> SchemeNeutral(Hct.fromInt(seedColor), isDark, 0.0)
            Variant.TONAL_SPOT -> SchemeTonalSpot(Hct.fromInt(seedColor), isDark, 0.0)
            Variant.VIBRANT -> SchemeVibrant(Hct.fromInt(seedColor), isDark, 0.0)
            Variant.EXPRESSIVE -> SchemeExpressive(Hct.fromInt(seedColor), isDark, 0.0)
            Variant.FIDELITY -> SchemeFidelity(Hct.fromInt(seedColor), isDark, 0.0)
            Variant.CONTENT -> SchemeContent(Hct.fromInt(seedColor), isDark, 0.0)
            Variant.RAINBOW -> SchemeRainbow(Hct.fromInt(seedColor), isDark, 0.0)
            Variant.FRUIT_SALAD -> SchemeFruitSalad(Hct.fromInt(seedColor), isDark, 0.0)
        }
        callback(ds)
    }
}