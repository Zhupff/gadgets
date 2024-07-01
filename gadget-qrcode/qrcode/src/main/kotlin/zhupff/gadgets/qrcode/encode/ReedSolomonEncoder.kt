package zhupff.gadgets.qrcode.encode

import zhupff.gadgets.qrcode.common.GaloisField

object ReedSolomonEncoder {

    fun encode(toEncode: IntArray, ecBytes: Int) {
        assert(ecBytes != 0)
        val dataBytes = toEncode.size - ecBytes
        assert(dataBytes > 0)
        val generator = buildGenerator(ecBytes)
        val infoCoefficients = toEncode.copyInto(IntArray(dataBytes), 0, 0, dataBytes)
        val info = GaloisField.buildPolynomial(infoCoefficients).multiplyByMonomial(ecBytes, 1)
        val remainder = info.divide(generator).second
        val coefficients = remainder.coefficients
        val numZeroCoefficients = ecBytes - coefficients.size
        for (i in 0 until numZeroCoefficients) {
            toEncode[dataBytes + i] = 0
        }
        coefficients.copyInto(toEncode, dataBytes + numZeroCoefficients, 0, coefficients.size)
    }

    private fun buildGenerator(degree: Int): GaloisField.Polynomial {
        val cachedGenerators = arrayListOf(GaloisField.ONE)
        if (degree >= cachedGenerators.size) {
            var lastGenerator = cachedGenerators.last()
            for (d in cachedGenerators.size..degree) {
                val nextGenerator = lastGenerator.multiply(GaloisField.buildPolynomial(intArrayOf(1, GaloisField.exp(d - 1))))
                cachedGenerators.add(nextGenerator)
                lastGenerator = nextGenerator
            }
        }
        return cachedGenerators[degree]
    }
}