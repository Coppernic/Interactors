package fr.coppernic.lib.interactors.barcode.zxing

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import java.nio.charset.Charset

/**
 * This class generates barcodes as Bitmap
 */
class BarcodeGenerator {
    private companion object {
        const val BLACK = 0xFF000000.toInt()
        const val WHITE = 0xFFFFFFFF.toInt()
    }

    /**
     * Generates a Bitmap representing content
     *
     * @param content Data to be encoded as a barcode bitmap
     * @param format Output format. see @BarcodeFormat for complete list of available formats.
     * @param width Width of the output bitmap
     * @param height Height of the ouput bitmap
     */
    fun generate(content: String, format: BarcodeFormat, width: Int, height: Int): Bitmap {
        // Encodes content into BitMatrix
        val writer = MultiFormatWriter()
        val bitMatrix = writer.encode(content, format, width, height)
        // Generates pixels from BitMatrix
        val bmpWidth = bitMatrix.width
        val bmpHeight = bitMatrix.height
        val pixels = IntArray(bmpWidth * bmpHeight)
        for (y in 0 until bmpHeight) {
            val offset = y * bmpWidth
            for (x in 0 until bmpWidth) {
                if (bitMatrix.get(x, y)) {
                    pixels[offset + x] = BLACK
                } else {
                    pixels[offset + x] = WHITE
                }
            }
        }
        // Creates Bitmap from pixels
        val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight)

        return bitmap
    }

    /**
     * Generates a Bitmap representing content
     *
     * @param content Data to be encoded as a barcode bitmap
     * @param format Output format. see @BarcodeFormat for complete list of available formats.
     * @param width Width of the output bitmap
     * @param height Height of the ouput bitmap
     */
    fun generate(content: ByteArray, format: BarcodeFormat, width: Int, height: Int): Bitmap {
        return generate(content.toString(Charset.defaultCharset()), format, width, height)
    }
}
