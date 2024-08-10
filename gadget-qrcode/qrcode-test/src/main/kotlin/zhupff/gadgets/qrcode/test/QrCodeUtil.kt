package zhupff.gadgets.qrcode.test

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BinaryBitmap
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QrCodeUtil {

    fun encode(content: String): Bitmap {
        val bitMatrix = QRCodeWriter().encode(
            content,
            ErrorCorrectionLevel.H,
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    fun decode(bitmap: Bitmap): Result? {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return QRCodeReader().decode(
            BinaryBitmap(
                HybridBinarizer(
                    RGBLuminanceSource(
                        width,
                        height,
                        pixels
                    )
                )
            ),
        )
    }

    fun decodeMultiple(bitmap: Bitmap): Array<Result>? {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return QRCodeMultiReader().decodeMultiple(
            BinaryBitmap(
                HybridBinarizer(
                    RGBLuminanceSource(
                        width,
                        height,
                        pixels
                    )
                )
            ),
        )
    }

    fun decode(data: ByteArray, width: Int, height: Int): Result? {
        return QRCodeReader().decode(
            BinaryBitmap(
                HybridBinarizer(
                    PlanarYUVLuminanceSource(
                        data,
                        width,
                        height,
                        0, 0, width, height,
                    )
                )
            ),
        )
    }

    fun decodeMultiple(data: ByteArray, width: Int, height: Int): Array<Result>? {
        return QRCodeMultiReader().decodeMultiple(
            BinaryBitmap(
                HybridBinarizer(
                    PlanarYUVLuminanceSource(
                        data,
                        width,
                        height,
                        0, 0, width, height,
                    )
                )
            ),
        )
    }
}