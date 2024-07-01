package zhupff.gadgets.qrcode.common

import com.google.zxing.qrcode.decoder.Version

enum class Mode(
    val bits: Int,
    private val bitsForVersions: IntArray,
) {

    TERMINATOR(
        0x00,
        intArrayOf(0, 0, 0),
    ),
    BYTE(
        0x04,
        intArrayOf(8, 16, 16),
    ),
    ECI(
        0x07,
        intArrayOf(0, 0, 0),
    ),
    ;

    fun getBits(version: Version) =
        if (version.versionNumber <= 9)
            bitsForVersions[0]
        else if (version.versionNumber <= 26)
            bitsForVersions[1]
        else
            bitsForVersions[2]

    companion object {
        fun forBits(bits: Int) = when (bits) {
            TERMINATOR.bits -> TERMINATOR
            BYTE.bits -> BYTE
            ECI.bits -> ECI
            else -> throw IllegalArgumentException()
        }
    }
}