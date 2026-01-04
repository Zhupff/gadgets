package gadget.theme.mcu.quantize

import gadget.theme.mcu.color.ARGB
import gadget.theme.mcu.color.HCT
import java.util.Arrays
import java.util.function.Supplier
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

object Quantizer {

    private val CACHES = HashMap<String, Int>()

    fun quantize(id: String, pixels: Supplier<IntArray>): Int {
        var cache = CACHES[id] ?: 0
        if (cache == 0) {
            cache = quantize(pixels.get())
            synchronized(CACHES) {
                CACHES[id] = cache
            }
        }
        return cache
    }

    private fun quantize(pixels: IntArray): Int {
        val maxColorCount = 128
        val cluster = Cluster(pixels)
        val colors = Box.create(cluster, maxColorCount)
        val colorPopulation = cluster.solve(colors, maxColorCount)
        return cluster.score(colorPopulation).firstOrNull() ?: 0
    }

    private fun index(r: Int, g: Int, b: Int) = (r shl 10) + (r shl 6)  + r + (g shl 5) + g + b


    private class Cluster(pixels: IntArray) {
        private val pixel2count = LinkedHashMap<Int, Int>(64)
        private val pixelsArray = IntArray(pixels.size)
        private val pointsArray = Array(pixels.size) { DoubleArray(3) }
        private var pointCount = 0
        val weights = IntArray(35937)
        val momentR = IntArray(35937)
        val momentG = IntArray(35937)
        val momentB = IntArray(35937)
        val moments = DoubleArray(35937)
        init {
            for (i in pixels.indices) {
                val pixel = pixels[i]
                val count = pixel2count[pixel] ?: 0
                if (count == 0) {
                    pointsFrom(pixel, pointsArray[pointCount])
                    pixelsArray[pointCount] = pixel
                    pointCount++
                }
                pixel2count[pixel] = count + 1
            }
            pixel2count.forEach { (color, count) ->
                val r = (color shr 16) and 255
                val g = (color shr 8) and 255
                val b = color and 255
                val index = index((r shr 3) + 1, (g shr 3) + 1, (b shr 3) + 1)
                weights[index] += count
                momentR[index] += (r * count)
                momentG[index] += (g * count)
                momentB[index] += (b * count)
                moments[index] += (count * (r * r + g * g + b * b))
            }
            for (r in 1 until 33) {
                val area1 = IntArray(33)
                val areaR = IntArray(33)
                val areaG = IntArray(33)
                val areaB = IntArray(33)
                val area2 = DoubleArray(33)
                for (g in 1 until 33) {
                    var line1 = 0
                    var lineR = 0
                    var lineG = 0
                    var lineB = 0
                    var line2 = 0.0
                    for (b in 1 until 33) {
                        val index1 = index(r, g, b)
                        line1 += weights[index1]
                        lineR += momentR[index1]
                        lineG += momentG[index1]
                        lineB += momentB[index1]
                        line2 += moments[index1]
                        area1[b] += line1
                        areaR[b] += lineR
                        areaG[b] += lineG
                        areaB[b] += lineB
                        area2[b] += line2
                        val index2 = index(r - 1, g, b)
                        weights[index1] = weights[index2] + area1[b]
                        momentR[index1] = momentR[index2] + areaR[b]
                        momentG[index1] = momentG[index2] + areaG[b]
                        momentB[index1] = momentB[index2] + areaB[b]
                        moments[index1] = moments[index2] + area2[b]
                    }
                }
            }
        }

        fun solve(colors: List<Int>, maxColorCount: Int): Map<Int, Int> {
            val random = java.util.Random(0x42688)
            val counts = IntArray(pointCount)
            for (i in 0 until pointCount) {
                counts[i] = pixel2count[pixelsArray[i]]!!
            }
            var clusterCount = min(pointCount, maxColorCount)
            if (colors.isNotEmpty()) {
                clusterCount = min(clusterCount, colors.size)
            }
            val clusters = Array(clusterCount) { DoubleArray(3) }
            for (i in colors.indices) {
                pointsFrom(colors[i], clusters[i])
            }
            val clusterIndices = IntArray(pointCount)
            for (i in 0 until pointCount) {
                clusterIndices[i] = random.nextInt(clusterCount)
            }
            val distanceToIndexMatrix = Array(clusterCount) { Array(clusterCount) { Distance() } }
            val pixelCountSums = IntArray(clusterCount)
            for (iteration in 0 until 10) {
                for (i in 0 until clusterCount) {
                    for (j in i + 1 until clusterCount) {
                        val distance = distance(clusters[i], clusters[j])
                        distanceToIndexMatrix[j][i].index = i
                        distanceToIndexMatrix[j][i].distance = distance
                        distanceToIndexMatrix[i][j].index = j
                        distanceToIndexMatrix[i][j].distance = distance
                    }
                    Arrays.sort(distanceToIndexMatrix[i])
                }
                var pointsMoved = 0
                for (i in 0 until pointCount) {
                    val point = pointsArray[i]
                    val previousClusterIndex = clusterIndices[i]
                    val previousCluster = clusters[previousClusterIndex]
                    val previousDistance = distance(point, previousCluster)
                    var minimumDistance = previousDistance
                    var newClusterIndex = -1
                    for (j in 0 until clusterCount) {
                        if (distanceToIndexMatrix[previousClusterIndex][j].distance >= 4 * previousDistance) {
                            continue
                        }
                        val distance = distance(point, clusters[j])
                        if (distance < minimumDistance) {
                            minimumDistance = distance
                            newClusterIndex = j
                        }
                    }
                    if (newClusterIndex != -1) {
                        val distanceChange = abs(sqrt(minimumDistance) - sqrt(previousDistance))
                        if (distanceChange > 3.0) {
                            pointsMoved++
                            clusterIndices[i] = newClusterIndex
                        }
                    }
                }
                if (pointsMoved == 0 && iteration != 0) {
                    break
                }
                val componentASums = DoubleArray(clusterCount)
                val componentBSums = DoubleArray(clusterCount)
                val componentCSums = DoubleArray(clusterCount)
                pixelCountSums.fill(0)
                for (i in 0 until pointCount) {
                    val clusterIndex = clusterIndices[i]
                    val point = pointsArray[i]
                    val count = counts[i]
                    pixelCountSums[clusterIndex] += count
                    componentASums[clusterIndex] += (point[0] * count)
                    componentBSums[clusterIndex] += (point[1] * count)
                    componentCSums[clusterIndex] += (point[2] * count)
                }
                for (i in 0 until clusterCount) {
                    val count = pixelCountSums[i]
                    if (count == 0) {
                        clusters[i][0] = 0.0
                        clusters[i][1] = 0.0
                        clusters[i][2] = 0.0
                        continue
                    }
                    clusters[i][0] = componentASums[i] / count
                    clusters[i][1] = componentBSums[i] / count
                    clusters[i][2] = componentCSums[i] / count
                }
            }
            val colorPopulation = LinkedHashMap<Int, Int>()
            for (i in 0 until clusterCount) {
                val count = pixelCountSums[i]
                if (count == 0) {
                    continue
                }
                val possibleNewCluster = pointsTo(clusters[i])
                if (colorPopulation.containsKey(possibleNewCluster)) {
                    continue
                }
                colorPopulation[possibleNewCluster] = count
            }
            return colorPopulation
        }

        fun score(colorPopulation: Map<Int, Int>, desired: Int = 4): List<Int> {
            val colorHCTs = ArrayList<HCT>()
            val huePopulation = IntArray(360)
            var populationSum = 0.0
            colorPopulation.forEach { (k, v) ->
                val hct = HCT(ARGB(k))
                colorHCTs.add(hct)
                val hue = Math.floor(hct.h).toInt()
                huePopulation[hue] += v
                populationSum += v
            }
            val hueProportions = DoubleArray(360)
            for (hue in 0 until 360) {
                val proportion = huePopulation[hue] / populationSum
                for (i in hue - 14 until hue + 16) {
                    hueProportions[sanitizeDegreesInt(i)] += proportion
                }
            }
            val scoredHCTs = ArrayList<Pair<HCT, Double>>()
            for (hct in colorHCTs) {
                val hue = sanitizeDegreesInt(Math.round(hct.h).toInt())
                val proportion = hueProportions[hue]
                if (hct.c < 5.0 || proportion <= 0.01) {
                    continue
                }
                val proportionScore = proportion * 100.0 * 0.7
                val chromaWeight = if (hct.c < 48.0) 0.1 else 0.3
                val chromaScore = (hct.c - 48.0) * chromaWeight
                scoredHCTs.add(hct to (proportionScore + chromaScore))
            }
            scoredHCTs.sortByDescending { it.second }
            val chosenHCTs = ArrayList<HCT>()
            for (degrees in 90 downTo 15) {
                chosenHCTs.clear()
                for ((hct, _) in scoredHCTs) {
                    var hasDuplicateHue = false
                    for (chosenHCT in chosenHCTs) {
                        if (180.0 - abs(abs(hct.h - chosenHCT.h) - 180.0) < degrees) {
                            hasDuplicateHue = true
                            break
                        }
                    }
                    if (!hasDuplicateHue) {
                        chosenHCTs.add(hct)
                    }
                    if (chosenHCTs.size > desired) {
                        break
                    }
                }
                if (chosenHCTs.size >= desired) {
                    break
                }
            }
            return chosenHCTs.map { it.argb.value }
        }

        private fun pointsFrom(pixel: Int, points: DoubleArray) {
            val linearR = ARGB.linearized((pixel shr 16) and 255)
            val linearG = ARGB.linearized((pixel shr 8) and 255)
            val linearB = ARGB.linearized(pixel and 255)
            val x = (0.41233895 * linearR + 0.35762064 * linearG + 0.18051042 * linearB) / 95.047
            val y = (0.2126     * linearR + 0.7152     * linearG + 0.0722     * linearB) / 100.0
            val z = (0.01932141 * linearR + 0.11916382 * linearG + 0.95034478 * linearB) / 108.883
            val xF = if (x > 216.0 / 24389.0) x.pow(1.0 / 3.0) else (24389.0 / 27.0 * x + 16.0) / 116.0
            val yF = if (y > 216.0 / 24389.0) y.pow(1.0 / 3.0) else (24389.0 / 27.0 * y + 16.0) / 116.0
            val zF = if (z > 216.0 / 24389.0) z.pow(1.0 / 3.0) else (24389.0 / 27.0 * z + 16.0) / 116.0
            points[0] = 116.0 * yF - 16.0
            points[1] = 500.0 * (xF - yF)
            points[2] = 200.0 * (yF - zF)
        }

        private fun pointsTo(points: DoubleArray): Int {
            val yF = (points[0] + 16.0) / 116.0
            val xF = points[1] / 500.0 + yF
            val zF = yF - points[2] / 200.0
            val x = (if (xF * xF * xF > 216.0 / 24389.0) xF * xF * xF else (116.0 * xF - 16.0) / (24389.0 / 27.0)) * 95.047
            val y = (if (yF * yF * yF > 216.0 / 24389.0) yF * yF * yF else (116.0 * yF - 16.0) / (24389.0 / 27.0)) * 100.0
            val z = (if (zF * zF * zF > 216.0 / 24389.0) zF * zF * zF else (116.0 * zF - 16.0) / (24389.0 / 27.0)) * 108.883
            val linearR = ( 3.2413774792388685  * x) + (-1.5376652402851851  * y) + (-0.49885366846268053 * z)
            val linearG = (-0.9691452513005321  * x) + ( 1.8758853451067872  * y) + ( 0.04156585616912061 * z)
            val linearB = ( 0.05562093689691305 * x) + (-0.20395524564742123 * y) + ( 1.0571799111220335  * z)
            return ((255 and 255) shl 24) or ((ARGB.delinearized(linearR) and 255) shl 16) or ((ARGB.delinearized(linearG) and 255) shl 8) or (ARGB.delinearized(linearB) and 255)
        }

        private fun distance(p1: DoubleArray, p2: DoubleArray): Double {
            val x = p1[0] - p2[0]
            val y = p1[1] - p2[1]
            val z = p1[2] - p2[2]
            return x * x + y * y + z * z
        }

        private fun sanitizeDegreesInt(degrees: Int): Int {
            var degrees = degrees % 360
            if (degrees < 0) {
                degrees += 360
            }
            return degrees
        }
    }

    private class Box(
        var r0: Int = 0, var r1: Int = 0,
        var g0: Int = 0, var g1: Int = 0,
        var b0: Int = 0, var b1: Int = 0,
        var vol: Int = 0,
    ) {
        companion object {
            fun create(cluster: Cluster, maxColorCount: Int): List<Int> {
                val cubes = Array(maxColorCount) { Box() }
                cubes[0].r1 = 32; cubes[0].g1 = 32; cubes[0].b1 = 32
                val volumes = DoubleArray(maxColorCount)
                var generatedColorCount = maxColorCount
                var i = 1; var next = 0
                while (i < maxColorCount) {
                    if (cubes[next].cut(cubes[i], cluster)) {
                        volumes[next] = if (cubes[next].vol > 1.0) cubes[next].variance(cluster) else 0.0
                        volumes[i] = if (cubes[i].vol > 1.0) cubes[i].variance(cluster) else 0.0
                    } else {
                        volumes[next] = 0.0
                        i--
                    }
                    next = 0
                    var temp = volumes[0]
                    for (j in 1..i) {
                        if (volumes[j] > temp) {
                            temp = volumes[j]
                            next = j
                        }
                    }
                    if (temp <= 0.0) {
                        generatedColorCount = i + 1
                        break
                    }
                    i++
                }
                val colors = ArrayList<Int>()
                i = 0
                while (i < generatedColorCount) {
                    val cube = cubes[i]
                    val weight = cube.volume(cluster.weights)
                    if (weight > 0) {
                        val r = cube.volume(cluster.momentR) / weight
                        val g = cube.volume(cluster.momentG) / weight
                        val b = cube.volume(cluster.momentB) / weight
                        colors.add((255 shl 24) or ((r and 255) shl 16) or ((g and 255) shl 8) or (b and 255))
                    }
                    i++
                }
                return colors
            }
        }

        private fun cut(other: Box, cluster: Cluster): Boolean {
            val wholeR = volume(cluster.momentR)
            val wholeG = volume(cluster.momentG)
            val wholeB = volume(cluster.momentB)
            val wholeW = volume(cluster.weights)
            val (locationR, maximumR) = maximize(cluster, 'R', r0 + 1, r1, wholeR, wholeG, wholeB, wholeW)
            val (locationG, maximumG) = maximize(cluster, 'G', g0 + 1, g1, wholeR, wholeG, wholeB, wholeW)
            val (locationB, maximumB) = maximize(cluster, 'B', b0 + 1, b1, wholeR, wholeG, wholeB, wholeW)
            val direction = if (maximumR >= maximumG && maximumR >= maximumB) {
                if (locationR < 0) {
                    return false
                }
                'R'
            } else if (maximumG >= maximumR && maximumG >= maximumB) {
                'G'
            } else 'B'
            other.r1 = r1; other.g1 = g1; other.b1 = b1
            when (direction) {
                'R' -> {
                    r1 = locationR
                    other.r0 = r1; other.g0 = g0; other.b0 = b0
                }
                'G' -> {
                    g1 = locationG
                    other.r0 = r0; other.g0 = g1; other.b0 = b0
                }
                'B' -> {
                    b1 = locationB
                    other.r0 = r0; other.g0 = g0; other.b0 = b1
                }
            }
            vol = (r1 - r0) * (g1 - g0) * (b1 - b0)
            other.vol = (other.r1 - other.r0) * (other.g1 - other.g0) * (other.b1 - other.b0)
            return true
        }

        private fun maximize(cluster: Cluster, direction: Char, first: Int, last: Int, wholeR: Int, wholeG: Int, wholeB: Int, wholeW: Int): Pair<Int, Double> {
            val bottomR = bottom(direction, cluster.momentR)
            val bottomG = bottom(direction, cluster.momentG)
            val bottomB = bottom(direction, cluster.momentB)
            val bottomW = bottom(direction, cluster.weights)
            var max = 0.0
            var cut = -1
            var halfR = 0
            var halfG = 0
            var halfB = 0
            var halfW = 0
            for (i in first until last) {
                halfR = bottomR + top(direction, i, cluster.momentR)
                halfG = bottomG + top(direction, i, cluster.momentG)
                halfB = bottomB + top(direction, i, cluster.momentB)
                halfW = bottomW + top(direction, i, cluster.weights)
                if (halfW == 0) {
                    continue
                }
                var tempNumerator: Double = (halfR * halfR + halfG * halfG + halfB * halfB).toDouble()
                var tempDenominator: Double = halfW.toDouble()
                var temp = tempNumerator / tempDenominator
                halfR = wholeR - halfR
                halfG = wholeG - halfG
                halfB = wholeB - halfB
                halfW = wholeW - halfW
                if (halfW == 0) {
                    continue
                }
                tempNumerator= (halfR * halfR + halfG * halfG + halfB * halfB).toDouble()
                tempDenominator = halfW.toDouble()
                temp += (tempNumerator / tempDenominator)
                if (temp > max) {
                    max = temp
                    cut = i
                }
            }
            return cut to max
        }

        private fun top(direction: Char, position: Int, moment: IntArray): Int = when (direction) {
            'R' -> moment[index(position, g1, b1)] - moment[index(position, g1, b0)] - moment[index(position, g0, b1)] + moment[index(position, g0, b0)]
            'G' -> moment[index(r1, position, b1)] - moment[index(r1, position, b0)] - moment[index(r0, position, b1)] + moment[index(r0, position, b0)]
            'B' -> moment[index(r1, g1, position)] - moment[index(r1, g0, position)] - moment[index(r0, g1, position)] + moment[index(r0, g0, position)]
            else -> 0
        }

        private fun bottom(direction: Char, moment: IntArray): Int = when (direction) {
            'R' -> -moment[index(r0, g1, b1)] + moment[index(r0, g1, b0)] + moment[index(r0, g0, b1)] - moment[index(r0, g0, b0)]
            'G' -> -moment[index(r1, g0, b1)] + moment[index(r1, g0, b0)] + moment[index(r0, g0, b1)] - moment[index(r0, g0, b0)]
            'B' -> -moment[index(r1, g1, b0)] + moment[index(r1, g0, b0)] + moment[index(r0, g1, b0)] - moment[index(r0, g0, b0)]
            else -> 0
        }

        private fun variance(cluster: Cluster): Double {
            val r = volume(cluster.momentR)
            val g = volume(cluster.momentG)
            val b = volume(cluster.momentB)
            val x = cluster.moments[index(r1, g1, b1)] -
                    cluster.moments[index(r1, g1, b0)] -
                    cluster.moments[index(r1, g0, b1)] +
                    cluster.moments[index(r1, g0, b0)] -
                    cluster.moments[index(r0, g1, b1)] +
                    cluster.moments[index(r0, g1, b0)] +
                    cluster.moments[index(r0, g0, b1)] -
                    cluster.moments[index(r0, g0, b0)]
            val hypotenuse = r * r + g * g + b * b
            val volume = volume(cluster.weights).toDouble()
            return x - hypotenuse / volume
        }

        private fun volume(moment: IntArray): Int =
            moment[index(r1, g1, b1)] -
            moment[index(r1, g1, b0)] -
            moment[index(r1, g0, b1)] +
            moment[index(r1, g0, b0)] -
            moment[index(r0, g1, b1)] +
            moment[index(r0, g1, b0)] +
            moment[index(r0, g0, b1)] -
            moment[index(r0, g0, b0)]
    }

    private data class Distance(
        var index: Int = -1,
        var distance: Double = -1.0,
    ) : Comparable<Distance> {
        override fun compareTo(other: Distance): Int = this.distance.compareTo(other.distance)
    }
}