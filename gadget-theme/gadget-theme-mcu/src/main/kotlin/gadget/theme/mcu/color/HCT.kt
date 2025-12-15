package gadget.theme.mcu.color

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

internal class HCT(
    val argb: ARGB,
) {
    companion object {
        private val CRITICAL_PLANES = doubleArrayOf(
            0.015176349177441876, 0.045529047532325624, 0.07588174588720938, 0.10623444424209313,
            0.13658714259697685 , 0.16693984095186062 , 0.19729253930674434, 0.2276452376616281 ,
            0.2579979360165119  , 0.28835063437139563 , 0.3188300904430532 , 0.350925934958123  ,
            0.3848314933096426  , 0.42057480301049466 , 0.458183274052838  , 0.4976837250274023 ,
            0.5391024159806381  , 0.5824650784040898  , 0.6277969426914107 , 0.6751227633498623 ,
            0.7244668422128921  , 0.775853049866786   , 0.829304845476233  , 0.8848452951698498 ,
            0.942497089126609   , 1.0022825574869039  , 1.0642236851973577 , 1.1283421258858297 ,
            1.1946592148522128  , 1.2631959812511864  , 1.3339731595349034 , 1.407011200216447  ,
            1.4823302800086415  , 1.5599503113873272  , 1.6398909516233677 , 1.7221716113234105 ,
            1.8068114625156377  , 1.8938294463134073  , 1.9832442801866852 , 2.075074464868551  ,
            2.1693382909216234  , 2.2660538449872063  , 2.36523901573795   , 2.4669114995532007 ,
            2.5710888059345764  , 2.6777882626779785  , 2.7870270208169257 , 2.898822059350997  ,
            3.0131901897720907  , 3.1301480604002863  , 3.2497121605402226 , 3.3718988244681087 ,
            3.4967242352587946  , 3.624204428461639   , 3.754355295633311  , 3.887192587735158  ,
            4.022731918402185   , 4.160988767090289   , 4.301978482107941  , 4.445716283538092  ,
            4.592217266055746   , 4.741496401646282   , 4.893568542229298  , 5.048448422192488  ,
            5.20615066083972    , 5.3666897647573375  , 5.5300801301023865 , 5.696336044816294  ,
            5.865471690767354   , 6.037501145825082   , 6.212438385869475  , 6.390297286737924  ,
            6.571091626112461   , 6.7548350853498045  , 6.941541251256611  , 7.131223617812143  ,
            7.323895587840543   , 7.5195704746346665  , 7.7182615035334345 , 7.919981813454504  ,
            8.124744458384042   , 8.332562408825165   , 8.543448553206703  , 8.757415699253682  ,
            8.974476575321063   , 9.194643831691977   , 9.417930041841839  , 9.644347703669503  ,
            9.873909240696694   , 10.106627003236781  , 10.342513269534024 , 10.58158024687427  ,
            10.8238400726681    , 11.069304815507364  , 11.317986476196008 , 11.569896988756009 ,
            11.825048221409341  , 12.083451977536606  , 12.345119996613247 , 12.610063955123938 ,
            12.878295467455942  , 13.149826086772048  , 13.42466730586372  , 13.702830557985108 ,
            13.984327217668513  , 14.269168601521828  , 14.55736596900856  , 14.848930523210871 ,
            15.143873411576273  , 15.44220572664832   , 15.743938506781891 , 16.04908273684337  ,
            16.35764934889634   , 16.66964922287304   , 16.985093187232053 , 17.30399201960269  ,
            17.62635644741625   , 17.95219714852476   , 18.281524751807332 , 18.614349837764564 ,
            18.95068293910138   , 19.290534541298456  , 19.633915083172692 , 19.98083495742689  ,
            20.331304511189067  , 20.685334046541502  , 21.042933821039977 , 21.404114048223256 ,
            21.76888489811322   , 22.137256497705877  , 22.50923893145328  , 22.884842241736916 ,
            23.264076429332462  , 23.6469514538663    , 24.033477234264016 , 24.42366364919083  ,
            24.817520537484558  , 25.21505769858089   , 25.61628489293138  , 26.021211842414342 ,
            26.429848230738664  , 26.842203703840827  , 27.258287870275353 , 27.678110301598522 ,
            28.10168053274597   , 28.529008062403893  , 28.96010235337422  , 29.39497283293396  ,
            29.83362889318845   , 30.276079891419332  , 30.722335150426627 , 31.172403958865512 ,
            31.62629557157785   , 32.08401920991837   , 32.54558406207592  , 33.010999283389665 ,
            33.4802739966603    , 33.953417292456834  , 34.430438229418264 , 34.911345834551085 ,
            35.39614910352207   , 35.88485700094671   , 36.37747846067349  , 36.87402238606382  ,
            37.37449765026789   , 37.87891309649659   , 38.38727753828926  , 38.89959975977785  ,
            39.41588851594697   , 39.93615253289054   , 40.460400508064545 , 40.98864111053629  ,
            41.520882981230194  , 42.05713473317016   , 42.597404951718396 , 43.141702194811224 ,
            43.6900349931913    , 44.24241185063697   , 44.798841244188324 , 45.35933162437017  ,
            45.92389141541209   , 46.49252901546552   , 47.065252796817916 , 47.64207110610409  ,
            48.22299226451468   , 48.808024568002054  , 49.3971762874833   , 49.9904556690408   ,
            50.587870934119984  , 51.189430279724725  , 51.79514187861014  , 52.40501387947288  ,
            53.0190544071392    , 53.637271562750364  , 54.259673423945976 , 54.88626804504493  ,
            55.517063457223934  , 56.15206766869424   , 56.79128866487574  , 57.43473440856916  ,
            58.08241284012621   , 58.734331877617365  , 59.39049941699807  , 60.05092333227251  ,
            60.715611475655585  , 61.38457167773311   , 62.057811747619894 , 62.7353394731159   ,
            63.417162620860914  , 64.10328893648692   , 64.79372614476921  , 65.48848194977529  ,
            66.18756403501224   , 66.89098006357258   , 67.59873767827808  , 68.31084450182222  ,
            69.02730813691093   , 69.74813616640164   , 70.47333615344107  , 71.20291564160104  ,
            71.93688215501312   , 72.67524319850172   , 73.41800625771542  , 74.16517879925733  ,
            74.9167682708136    , 75.67278210128072   , 76.43322770089146  , 77.1981124613393   ,
            77.96744375590167   , 78.74122893956174   , 79.51947534912904  , 80.30219030335869  ,
            81.08938110306934   , 81.88105503125999   , 82.67721935322541  , 83.4778813166706   ,
            84.28304815182372   , 85.09272707154808   , 85.90692527145302  , 86.72564993000343  ,
            87.54890820862819   , 88.3767072518277    , 89.2090541872801   , 90.04595612594655  ,
            90.88742016217518   , 91.73345337380438   , 92.58406282226491  , 93.43925555268066  ,
            94.29903859396902   , 95.16341895893969   , 96.03240364439274  , 96.9059996312159   ,
            97.78421388448044   , 98.6670533535366    , 99.55452497210776  ,
        )

        fun solve(hue: Double, chroma: Double, tone: Double): ARGB {
            if (chroma < 0.0001 || tone < 0.0001 || tone > 99.9999) {
                val component = ((tone + 16.0) / 116.0).let {
                    ARGB.delinearized(if (it * it * it > 216.0 / 24389.0) {
                        it * it * it
                    } else {
                        (116.0 * it - 16.0) / (24389.0 / 27.0)
                    } * 100.0)
                }
                return ARGB(255, component, component, component)
            }
            val radians = (hue % 360.0).let {
                if (it < 0.0) {
                    it + 360.0
                } else {
                    it
                }
            } / 180.0 * Math.PI
            val result: Int = ((tone + 16.0) / 116.0).let {
                if (it * it * it > 216.0 / 24389.0) {
                    it * it * it
                } else {
                    (116.0 * it - 16.0) / (24389.0 / 27.0)
                } * 100.0
            }.let {
                var j = sqrt(it) * 11.0
                val sin = sin(radians)
                val cos = cos(radians)
                val p1 = 0.25 * (cos(radians + 2.0) + 3.8) * (50000.0 / 13.0) * 1.0169191804458755
                for (i in 0 until 5) {
                    val alpha = if (j == 0.0) {
                        0.0
                    } else {
                        chroma / sqrt(j / 100.0)
                    }
                    val t = (alpha * (1.0 / (1.64 - 0.29.pow(0.18418651851244416)).pow(0.73))).pow(1.0 / 0.9)
                    val p2 = 29.980997194447333 * (j / 100.0).pow(1.0 / 0.69 / 1.909169568483652) / 1.0169191804458755
                    val gamma = 23.0 * (p2 + 0.305) * t / (23.0 * p1 + 11.0 * t * cos + 108.0 * t * sin)
                    val a = gamma * cos
                    val b = gamma * sin
                    val rA = ((460.0 * p2 + 451.0 * a + 288.0  * b) / 1403.0).let { adapted ->
                        val abs = abs(adapted)
                        sign(adapted) * max(0.0, 27.13 * abs / (400.0 - abs)).pow(1.0 / 0.42)
                    }
                    val gA = ((460.0 * p2 - 891.0 * a - 261.0  * b) / 1403.0).let { adapted ->
                        val abs = abs(adapted)
                        sign(adapted) * max(0.0, 27.13 * abs / (400.0 - abs)).pow(1.0 / 0.42)
                    }
                    val bA = ((460.0 * p2 - 220.0 * a - 6300.0 * b) / 1403.0).let { adapted ->
                        val abs = abs(adapted)
                        sign(adapted) * max(0.0, 27.13 * abs / (400.0 - abs)).pow(1.0 / 0.42)
                    }
                    val rL = (rA * 1373.2198709594231) + (gA * -1100.4251190754821) + (bA * -7.278681089101213)
                    val gL = (rA * -271.815969077903 ) + (gA * 559.6580465940733  ) + (bA * -32.46047482791194)
                    val bL = (rA * 1.9622899599665666) + (gA * -57.173814538844006) + (bA * 308.7233197812385 )
                    if (rL < 0.0 || gL < 0.0 || bL < 0.0) {
                        return@let 0
                    }
                    val fnj = 0.2126 * rL + 0.7152 * gL + 0.0722 * bL
                    if (fnj <= 0) {
                        return@let 0
                    }
                    if (i == 4 || abs(fnj - it) < 0.002) {
                        if (rL > 100.01 || gL > 100.01 || bL > 100.01) {
                            return@let 0
                        }
                        return@let ARGB(255, ARGB.delinearized(rL), ARGB.delinearized(gL), ARGB.delinearized(bL)).value
                    }
                    j -= (fnj - it) * j / (2 * fnj)
                }
                0
            }
            if (result != 0) {
                return ARGB(result)
            }
            return ((tone + 16.0) / 116.0).let {
                if (it * it * it > 216.0 / 24389.0) {
                    it * it * it
                } else {
                    (116.0 * it - 16.0) / (24389.0 / 27.0)
                } * 100.0
            }.let { y ->
                var left = doubleArrayOf(-1.0, -1.0, -1.0)
                var right = left
                var leftHue = 0.0
                var rightHue = 0.0
                var initialized = false
                var uncut = true
                for (n in 0 until 12) {
                    // 0.2126, 0.7152, 0.0722
                    val mid = doubleArrayOf(-1.0, -1.0, -1.0)
                    if (n < 4) {
                        val g = if (n == 0 || n == 1) 0.0 else 100.0
                        val b = if (n == 0 || n == 2) 0.0 else 100.0
                        val r = (y - g * 0.7152 - b * 0.0722) / 0.2126
                        if (r in 0.0..100.0) {
                            mid[0] = r; mid[1] = g; mid[2] = b;
                        }
                    } else if (n < 8) {
                        val b = if (n == 4 || n == 5) 0.0 else 100.0
                        val r = if (n == 4 || n == 6) 0.0 else 100.0
                        val g = (y - r * 0.2126 - b * 0.0722) / 0.7152
                        if (g in 0.0..100.0) {
                            mid[0] = r; mid[1] = g; mid[2] = b;
                        }
                    } else {
                        val r = if (n == 8 || n == 9) 0.0 else 100.0
                        val g = if (n == 8 || n == 10) 0.0 else 100.0
                        val b = (y - r * 0.2126 - g * 0.7152) / 0.0722
                        if (b in 0.0..100.0) {
                            mid[0] = r; mid[1] = g; mid[2] = b;
                        }
                    }
                    if (mid[0] < 0.0) {
                        continue
                    }
                    val rA = (mid[0] * 0.001200833568784504   + mid[1] * 0.002389694492170889  + mid[2] * 0.0002795742885861124).let {
                        val component = abs(it).pow(0.42)
                        sign(it) * 400.0 * component / (component + 27.13)
                    }
                    val gA = (mid[0] * 0.0005891086651375999  + mid[1] * 0.0029785502573438758 + mid[2] * 0.0003270666104008398).let {
                        val component = abs(it).pow(0.42)
                        sign(it) * 400.0 * component / (component + 27.13)
                    }
                    val bA = (mid[0] * 0.00010146692491640572 + mid[1] * 0.0005364214359186694 + mid[2] * 0.0032979401770712076).let {
                        val component = abs(it).pow(0.42)
                        sign(it) * 400.0 * component / (component + 27.13)
                    }
                    val midHue = atan2((rA + gA - 2.0 * bA) / 9.0, (11.0 * rA + -12.0 * gA + bA) / 11.0)
                    if (!initialized) {
                        left = mid
                        right = mid
                        leftHue = midHue
                        rightHue = midHue
                        initialized = true
                        continue
                    }
                    if (uncut || ((midHue - leftHue) + Math.PI * 8) % (Math.PI * 2) < ((rightHue - leftHue) + Math.PI * 8) % (Math.PI * 2)) {
                        uncut = false
                        if (((radians - leftHue) + Math.PI * 8) % (Math.PI * 2) < ((midHue - leftHue) + Math.PI * 8) % (Math.PI * 2)) {
                            right = mid
                            rightHue = midHue
                        } else {
                            left = mid
                            leftHue = midHue
                        }
                    }
                }
                for (axis in 0 until 3) {
                    val leftAxis = left[axis]
                    val rightAxis = right[axis]
                    if (leftAxis != rightAxis) {
                        var lPlane: Int
                        var rPlane: Int
                        if (leftAxis < rightAxis) {
                            lPlane = floor(ARGB.delinearized2(leftAxis) - 0.5).toInt()
                            rPlane = ceil(ARGB.delinearized2(rightAxis) - 0.5).toInt()
                        } else {
                            lPlane = ceil(ARGB.delinearized2(leftAxis) - 0.5).toInt()
                            rPlane = floor(ARGB.delinearized2(rightAxis) - 0.5).toInt()
                        }
                        for (i in 0 until 8) {
                            if (abs(rPlane - lPlane) <= 1) {
                                break
                            }
                            val mPlane = floor((lPlane + rPlane) / 2.0).toInt()
                            val mid = CRITICAL_PLANES[mPlane].let { p ->
                                val t = (p - leftAxis) / (rightAxis - leftAxis)
                                doubleArrayOf(
                                    left[0] + (right[0] - left[0]) * t,
                                    left[1] + (right[1] - left[1]) * t,
                                    left[2] + (right[2] - left[2]) * t,
                                )
                            }
                            val rA = (mid[0] * 0.001200833568784504   + mid[1] * 0.002389694492170889  + mid[2] * 0.0002795742885861124).let {
                                val component = abs(it).pow(0.42)
                                sign(it) * 400.0 * component / (component + 27.13)
                            }
                            val gA = (mid[0] * 0.0005891086651375999  + mid[1] * 0.0029785502573438758 + mid[2] * 0.0003270666104008398).let {
                                val component = abs(it).pow(0.42)
                                sign(it) * 400.0 * component / (component + 27.13)
                            }
                            val bA = (mid[0] * 0.00010146692491640572 + mid[1] * 0.0005364214359186694 + mid[2] * 0.0032979401770712076).let {
                                val component = abs(it).pow(0.42)
                                sign(it) * 400.0 * component / (component + 27.13)
                            }
                            val midHue = atan2((rA + gA - 2.0 * bA) / 9.0, (11.0 * rA + -12.0 * gA + bA) / 11.0)
                            if (((radians - leftHue) + Math.PI * 8) % (Math.PI * 2) < ((midHue - leftHue) + Math.PI * 8) % (Math.PI * 2)) {
                                right = mid
                                rPlane = mPlane
                            } else {
                                left = mid
                                leftHue = midHue
                                lPlane = mPlane
                            }
                        }
                    }
                }

                ARGB(255,
                    ARGB.delinearized((left[0] + right[0]) / 2.0),
                    ARGB.delinearized((left[1] + right[1]) / 2.0),
                    ARGB.delinearized((left[2] + right[2]) / 2.0)
                )
            }
        }

        fun solve(hue: Double, chroma: Double): ARGB {
            val cache = HashMap<Int, Double>()
            var lowerTone: Int = 0
            var upperTone: Int = 100
            while (lowerTone < upperTone) {
                val midTone = (lowerTone + upperTone) / 2
                if (cache.getOrPut(midTone) {
                        HCT(solve(hue, 200.0, midTone.toDouble())).c
                    } >= chroma - 0.01) {
                    if (abs(lowerTone - 50) < abs(upperTone - 50)) {
                        upperTone = midTone
                    } else {
                        if (lowerTone == midTone) {
                            break
                        }
                        lowerTone = midTone
                    }
                } else {
                    if (cache.getOrPut(midTone) {
                            HCT(solve(hue, 200.0, midTone.toDouble())).c
                        } < cache.getOrPut(midTone + 1) {
                            HCT(solve(hue, 200.0, (midTone + 1).toDouble())).c
                        }) {
                        lowerTone = midTone + 1
                    } else {
                        upperTone = midTone
                    }
                }
            }
            return solve(hue, chroma, lowerTone.toDouble())
        }

        fun fix(hct: HCT): HCT {
            if (Math.round(hct.h) in 90L..111L && Math.round(hct.c) > 16L && Math.round(hct.t) < 65L) {
                return HCT(solve(hct.h, hct.c, 70.0))
            }
            return hct
        }

        fun isYellow(hue: Double): Boolean = hue >= 105.0 && hue < 125.0
    }

    val h: Double
    val c: Double
    val t: Double

    init {
        val cam16 = CAM16(argb)
        this.h = cam16.hue
        this.c = cam16.chroma
        this.t = 116.0 * ((ARGB.linearized(argb.r) * 0.2126 + ARGB.linearized(argb.g) * 0.7152 + ARGB.linearized(argb.b) * 0.0722) / 100.0).let {
            if (it > 216.0 / 24389.0) {
                it.pow(1.0 / 3.0)
            } else {
                (24389.0 / 27.0 * it + 16.0) / 116.0
            }
        } - 16.0
    }
}