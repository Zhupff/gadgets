package gadget.theme.mcu.scheme

import gadget.theme.mcu.color.ARGB
import gadget.theme.mcu.color.HCT
import gadget.theme.mcu.palette.Palette
import kotlin.math.min

abstract class Scheme private constructor(
    val source: Int,
    private val primaryPalette: Palette,
    private val secondaryPalette: Palette,
    private val tertiaryPalette: Palette,
    private val neutralPalette: Palette,
    private val variantPalette: Palette,
    private val errorPalette: Palette,
) {

    private constructor(hct: HCT) : this(
        hct.argb.value,
        Palette(hct.h, hct.c),
        Palette(hct.h, hct.c / 3.0),
        Palette(hct.h + 60.0, hct.c / 2.0),
        Palette(hct.h, min(hct.c / 12.0, 4.0)),
        Palette(hct.h, min(hct.c / 6.0, 8.0)),
//        Palette(hct.h, max(hct.c, 48.0)),
//        Palette(hct.h, 16.0),
//        Palette(hct.h + 60.0, 24.0),
//        Palette(hct.h, 4.0),
//        Palette(hct.h, 8.0),
        Palette(25.0, 84.0),
    )

    constructor(source: Int) : this(HCT(ARGB(source)))

    constructor(scheme: Scheme) : this(
        scheme.source,
        scheme.primaryPalette,
        scheme.secondaryPalette,
        scheme.tertiaryPalette,
        scheme.neutralPalette,
        scheme.variantPalette,
        scheme.errorPalette,
    )

    abstract val primary: Int
    abstract val onPrimary: Int
    abstract val primaryContainer: Int
    abstract val onPrimaryContainer: Int
    abstract val secondary: Int
    abstract val onSecondary: Int
    abstract val secondaryContainer: Int
    abstract val onSecondaryContainer: Int
    abstract val tertiary: Int
    abstract val onTertiary: Int
    abstract val tertiaryContainer: Int
    abstract val onTertiaryContainer: Int
    abstract val error: Int
    abstract val onError: Int
    abstract val errorContainer: Int
    abstract val onErrorContainer: Int
    abstract val background: Int
    abstract val onBackground: Int
    abstract val surface: Int
    abstract val onSurface: Int
    abstract val surfaceVariant: Int
    abstract val onSurfaceVariant: Int
    abstract val outline: Int
    abstract val outlineVariant: Int
    abstract val shadow: Int
    abstract val scrim: Int
    abstract val inversePrimary: Int
    abstract val inverseSecondary: Int
    abstract val inverseTertiary: Int
    abstract val inverseSurface: Int
    abstract val inverseOnSurface: Int

    protected fun primaryTone(tone: Int): Int = primaryPalette.tone(tone)

    protected fun secondaryTone(tone: Int): Int = secondaryPalette.tone(tone)

    protected fun tertiaryTone(tone: Int): Int = tertiaryPalette.tone(tone)

    protected fun neutralTone(tone: Int): Int = neutralPalette.tone(tone)

    protected fun variantTone(tone: Int): Int = variantPalette.tone(tone)

    protected fun errorTone(tone: Int): Int = errorPalette.tone(tone)
}