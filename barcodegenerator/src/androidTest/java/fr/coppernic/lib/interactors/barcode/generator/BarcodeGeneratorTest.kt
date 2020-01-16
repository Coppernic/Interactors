package fr.coppernic.lib.interactors.barcode.generator

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.zxing.BarcodeFormat
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class BarcodeGeneratorTest {
    /**
     * This test checks that the generated bitmap has correct dimensions
     */
    @Test
    fun generateStringTest() {
        val bmp = BarcodeGenerator().generate("123456",
                BarcodeFormat.QR_CODE,
                100,
                100)

        bmp.width shouldEqualTo 100
        bmp.height shouldEqualTo 100
    }

    /**
     * This test checks that the generated bitmap has correct dimensions
     */
    @Test
    fun generateByteArrayTest() {
        val bmp = BarcodeGenerator().generate(byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06),
                BarcodeFormat.QR_CODE,
                100,
                100)

        bmp.width shouldEqualTo 100
        bmp.height shouldEqualTo 100
    }

    /**
     * This test checks that an IllegalArgumentException is raised when width is negative
     */
    @Test
    fun generateStringNegativeWidthTest() {
        try {
            BarcodeGenerator().generate("123456",
                    BarcodeFormat.QR_CODE,
                    -1,
                    1)
            // If no exception is raised, test fails
            fail()
        } catch (ex: IllegalArgumentException) {
            // Test succeeds
        }
    }

    /**
     * This test checks that an IllegalArgumentException is raised when height is negative
     */
    @Test
    fun generateStringNegativeHeightTest() {
        try {
            BarcodeGenerator().generate("123456",
                    BarcodeFormat.QR_CODE,
                    1,
                    -1)
            // If no exception is raised, test fails
            fail()
        } catch (ex: IllegalArgumentException) {
            // Test succeeds
        }
    }
}
