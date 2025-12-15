import dislike.DislikeAnalyzer
import gadget.theme.mcu.color.ARGB
import gadget.theme.mcu.color.CAM16
import gadget.theme.mcu.color.HCT
import gadget.theme.mcu.palette.Palette
import hct.Cam16
import hct.Hct
import hct.ViewingConditions
import palettes.TonalPalette
import utils.ColorUtils
import kotlin.random.Random

fun main() {
    testAll()
}

private fun testAll() {
    testPalette1()
    testPalette2()
    testCam16()
    testCam16_2()
    testHCT1()
    testHCT2()
    testHCT3()
    testHCT4()
    testLinearized()
    testViewingConditions()
}

private fun testHCT4() {
    val random = Random(System.currentTimeMillis())
    val total = 100000000
    for (i in 0 until total) {
        val h = random.nextDouble(0.0, 360.0)
        val c = random.nextDouble(0.0, Double.MAX_VALUE)
        val t = random.nextDouble(0.0, 100.0)
        val hct1 = HCT.fix(HCT(HCT.solve(h, c, t)))
        val hct2 = DislikeAnalyzer.fixIfDisliked(Hct.from(h, c, t))
        val ok = hct1.argb.value.toString() == hct2.toInt().toString() &&
                hct1.h.toString() == hct2.hue.toString() &&
                hct1.c.toString() == hct2.chroma.toString() &&
                hct1.t.toString() == hct2.tone.toString()
        assert(ok) {
            "testHCT4 (${h}, ${c}, ${t}) failed: ${hct1.argb} ~ ${ARGB(hct2.toInt())}"
        }
    }
    println("testHCT4 ok!")
}

private fun testHCT3() {
    val h = 156.50804287960048
    val c = 1.329287827210199E307
    val t = 70.17034298666422
    val hct2 = Hct.from(h, c, t)
    val hct1 = HCT(HCT.solve(h, c, t))
    assert(hct1.argb.value.toString() == hct2.toInt().toString()) {
        "testHCT3 failed"
    }
    println("testHCT3 ok!")
}

private fun testHCT2() {
    val random = Random(System.currentTimeMillis())
    val total = 100000000
    for (i in 0 until total) {
        val h = random.nextDouble(0.0, 360.0)
        val c = random.nextDouble(0.0, Double.MAX_VALUE)
        val t = random.nextDouble(0.0, 100.0)
        val hct1 = HCT(HCT.solve(h, c, t))
        val hct2 = Hct.from(h, c, t)
        val ok = hct1.argb.value.toString() == hct2.toInt().toString() &&
            hct1.h.toString() == hct2.hue.toString() &&
            hct1.c.toString() == hct2.chroma.toString() &&
            hct1.t.toString() == hct2.tone.toString()
        assert(ok) {
            "testHCT2 (${h}, ${c}, ${t}) failed: ${hct1.argb} ~ ${ARGB(hct2.toInt())}"
        }
    }
    println("testHCT2 ok!")
}

private fun testHCT1() {
    val random = Random(System.currentTimeMillis())
    for (r in 0..255) {
        for (g in 0..255) {
            for (b in 0..255) {
                val argb = ARGB(random.nextInt(0, 255), r, g, b)
                val hct1 = HCT(argb)
                val hct2 = Hct.fromInt(argb.value)
                val ok = hct1.argb.value.toString() == hct2.toInt().toString() &&
                    hct1.h.toString() == hct2.hue.toString() &&
                    hct1.c.toString() == hct2.chroma.toString() &&
                    hct1.t.toString() == hct2.tone.toString()
                assert(ok) {
                    "testHCT1 argb=${argb}(${argb.value}) failed"
                }
            }
        }
    }
    println("testHCT1 ok!")
}

private fun testCam16_2() {
    val random = Random(System.currentTimeMillis())
    for (r in 0..255) {
        for (g in 0..255) {
            for (b in 0..255) {
                val argb = ARGB(random.nextInt(0, 255), r, g, b)
                val c1 = CAM16(argb)
                val c2 = Cam16.fromInt(argb.value)
                val ok = c1.hue.toString() == c2.hue.toString() &&
                    c1.chroma.toString() == c2.chroma.toString() &&
                    c1.lightness.toString() == c2.j.toString() &&
                    c1.brightness.toString() == c2.q.toString() &&
                    c1.colorfulness.toString() == c2.m.toString() &&
                    c1.saturation.toString() == c2.s.toString() &&
                    c1.stars.first.toString() == c2.jstar.toString() &&
                    c1.stars.second.toString() == c2.astar.toString() &&
                    c1.stars.third.toString() == c2.bstar.toString()
                assert(ok) {
                    "testCam16_2 argb=${argb}(${argb.value}) failed"
                }
            }
        }
    }
    println("testCam16_2 ok!")
}

private fun testCam16() {
    val random = Random(System.currentTimeMillis())
    val total = 10000
    for (i in 1..total) {
        val argb = ARGB(
            random.nextInt(0, 255),
            random.nextInt(0, 255),
            random.nextInt(0, 255),
            random.nextInt(0, 255)
        )
        val a = CAM16(argb)
        val b = Cam16.fromInt(argb.value)
        val ok = a.hue.toString() == b.hue.toString() &&
            a.chroma.toString() == b.chroma.toString() &&
            a.lightness.toString() == b.j.toString() &&
            a.brightness.toString() == b.q.toString() &&
            a.colorfulness.toString() == b.m.toString() &&
            a.saturation.toString() == b.s.toString() &&
            a.stars.first.toString() == b.jstar.toString() &&
            a.stars.second.toString() == b.astar.toString() &&
            a.stars.third.toString() == b.bstar.toString()
        assert(ok) {
            "testCam16argb=${argb}(${argb.value}) failed"
        }
    }
    println("testCam16 ok!")
}

private fun testViewingConditions() {
    val vc = ViewingConditions.DEFAULT
    println("-------- ViewConditions --------")
    println(vc)
    println("-------- ViewConditions --------")
}

private fun testLinearized() {
    val random = Random(System.currentTimeMillis())

    for (component in 0..255) {
        val a = ARGB.linearized(component).toString()
        val b = ColorUtils.linearized(component).toString()
        assert(a == b) {
            "$component linearized failed: ${a} != ${b}"
        }
    }

    for (i in 0..10000) {
        val component = random.nextDouble(0.0, 100.0)
        val a = ARGB.delinearized(component).toString()
        val b = ColorUtils.delinearized(component).toString()
        assert(a == b) {
            "$component delinearized failed: ${a} != ${b}"
        }
    }

    println("testLinearized ok!")
}

private fun testARGB() {
    val argb = ARGB(0xFE4A0115.toInt())
    println("${argb}, a=${argb.a}, r=${argb.r}, g=${argb.g}, b=${argb.b}")
    val argb1 = ARGB(argb.a, argb.r, argb.g, argb.b)
    println("${argb1}, a=${argb1.a}, r=${argb1.r}, g=${argb1.g}, b=${argb1.b}")
}

private fun testPalette1() {
    val startTimestamp = System.currentTimeMillis()
    val random = Random(System.currentTimeMillis())
    val total = 1000000
    for (i in 0 until total) {
        val h = random.nextDouble(0.0, 360.0)
        val c = random.nextDouble(0.0, Double.MAX_VALUE)
        val p1 = Palette(h, c)
        val p2 = TonalPalette.fromHueAndChroma(h, c)
        val ok = p1.hct.argb.value == p2.keyColor.toInt() &&
            p1.hct.h.toString() == p2.keyColor.hue.toString() &&
            p1.hct.c.toString() == p2.keyColor.chroma.toString() &&
            p1.hct.t.toString() == p2.keyColor.tone.toString() &&
            p1.hue.toString() == p2.hue.toString() &&
            p1.chroma.toString() == p2.chroma.toString()
        assert(ok) {
            "testPalette1 (${h}, ${c}) failed"
        }
    }
    val stopTimestamp = System.currentTimeMillis()
    println("testPalette1 ok! cost=${stopTimestamp - startTimestamp}ms")
}

private fun testPalette2() {
    val startTimestamp = System.currentTimeMillis()
    val argb = ARGB(0xFF608701.toInt())
    val p1 = Palette(HCT(argb))
    val p2 = TonalPalette.fromInt(argb.value)
    for (t in 0..100) {
        assert(p1.tone(t) == p2.tone(t)) {
            "testPalette2 tone=${t} failed"
        }
    }
    val stopTimestamp = System.currentTimeMillis()
    println("testPalette2 ok! cost=${stopTimestamp - startTimestamp}ms")
}