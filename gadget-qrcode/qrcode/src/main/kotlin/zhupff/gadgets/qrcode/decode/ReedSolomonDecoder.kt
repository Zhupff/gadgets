package zhupff.gadgets.qrcode.decode

import zhupff.gadgets.qrcode.common.GaloisField

object ReedSolomonDecoder {

    fun decode(received: IntArray, twoS: Int): Int {
        val poly = GaloisField.buildPolynomial(received)
        var noError = true
        val syndromeCoefficients = IntArray(twoS)
        for (i in syndromeCoefficients.indices) {
            val eval = poly.evaluateAt(GaloisField.exp(i))
            syndromeCoefficients[twoS - 1 - i] = eval
            if (eval != 0) noError = false
        }
        if (noError) return 0
        val syndrome = GaloisField.buildPolynomial(syndromeCoefficients)
        val (sigma, omega) = runEuclideanAlgorithm(GaloisField.buildMonomial(twoS, 1), syndrome, twoS)
        val errorLocations = findErrorLocations(sigma)
        val errorMagnitudes = findErrorMagnitudes(omega, errorLocations)
        for (i in errorLocations.indices) {
            val position = received.size - 1 - GaloisField.log(errorLocations[i])
            assert(position >= 0)
            received[position] = received[position] xor errorMagnitudes[i]
        }
        return errorLocations.size
    }

    private fun runEuclideanAlgorithm(A: GaloisField.Polynomial, B: GaloisField.Polynomial, R: Int): Pair<GaloisField.Polynomial, GaloisField.Polynomial> {
        val a: GaloisField.Polynomial = if (A.degree < B.degree) B else A
        val b: GaloisField.Polynomial = if (A.degree < B.degree) A else B
        var rLast = a
        var r = b
        var tLast = GaloisField.ZERO
        var t = GaloisField.ONE
        while (2 * r.degree >= R) {
            val rLastLast = rLast
            val tLastLast = tLast
            rLast = r
            tLast = t
            assert(!rLast.isZero)
            r = rLastLast
            var q = GaloisField.ZERO
            val denominatorLeadingTerm = rLast.getCoefficient(rLast.degree)
            val dltInverse = GaloisField.inverse(denominatorLeadingTerm)
            while (r.degree >= rLast.degree && !r.isZero) {
                val degreeDiff = r.degree - rLast.degree
                val scale = GaloisField.multiply(r.getCoefficient(r.degree), dltInverse)
                q = q.addOrSubtract(GaloisField.buildMonomial(degreeDiff, scale))
                r = r.addOrSubtract(rLast.multiplyByMonomial(degreeDiff, scale))
            }
            t = q.multiply(tLast).addOrSubtract(tLastLast)
            assert(r.degree < rLast.degree)
        }
        val sigmaTildeAtZero = t.getCoefficient(0)
        assert(sigmaTildeAtZero != 0)
        val inverse = GaloisField.inverse(sigmaTildeAtZero)
        return t.multiply(inverse) to r.multiply(inverse)
    }

    private fun findErrorLocations(errorLocator: GaloisField.Polynomial): IntArray {
        val numErrors = errorLocator.degree
        if (numErrors == 1) return intArrayOf(errorLocator.getCoefficient(1))
        val result = IntArray(numErrors)
        var ne = 0
        var i = 1
        while (i < GaloisField.SIZE && ne < numErrors) {
            if (errorLocator.evaluateAt(i) == 0) {
                result[ne] = GaloisField.inverse(i)
                ne += 1
            }
            i += 1
        }
        assert(ne == numErrors)
        return result
    }

    private fun findErrorMagnitudes(errorEvaluator: GaloisField.Polynomial, errorLocations: IntArray): IntArray {
        val s = errorLocations.size
        val result = IntArray(s)
        for (i in 0 until s) {
            val xiInverse = GaloisField.inverse(errorLocations[i])
            var denominator = 1
            for (j in 0 until s) {
                if (i != j) {
                    val term = GaloisField.multiply(errorLocations[j], xiInverse)
                    val termPlus1 = if ((term and 1) == 0) term or 1 else term and -2
                    denominator = GaloisField.multiply(denominator, termPlus1)
                }
            }
            result[i] = GaloisField.multiply(errorEvaluator.evaluateAt(xiInverse), GaloisField.inverse(denominator))
        }
        return result
    }
}