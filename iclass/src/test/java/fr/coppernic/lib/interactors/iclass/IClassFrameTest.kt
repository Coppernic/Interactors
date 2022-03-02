package fr.coppernic.lib.interactors.iclass

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Test

class IClassFrameTest {
    private val frameMifareTest = IClassFrame(
        byteArrayOf(
            0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x00,
            0x83.toByte(), 0x83.toByte(), 0x9D.toByte(), 0x66, 0x33, 0xD6.toByte()
        )
    )
    private val frameMifareCustomMaskTest = IClassFrame(
        byteArrayOf(
            0x00, 0x13, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0B, 0x9E.toByte(), 0x09, 0x81.toByte(), 0x07, 0x04,
            0x00, 0x49, 0x10.toByte(), 0x3D, 0x43, 0x70.toByte(),0xB8.toByte(), 0x63.toByte()
        ),true, 0xFFFFFFL
    )

    private val frameMifare3Test = IClassFrame(
        byteArrayOf(
            0x00, 0x13, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0B, 0x9E.toByte(), 0x09, 0x81.toByte(), 0x07, 0x04,
            0x00, 0x49, 0x04, 0x6B, 0x7B, 0xA0.toByte(),0x20, 0x29.toByte()
        ),true
    )
    private val frameHfTest26bit = IClassFrame(
        byteArrayOf(
            0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x06,
            0x3D, 0x80.toByte(), 0x05, 0x80.toByte(), 0xB4.toByte(), 0x98.toByte()
        )
    )
    private val frameHfTest26bit2 = IClassFrame(
        byteArrayOf(
            0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x06,
            0x80.toByte(), 0x80.toByte(), 0x08, 0xC0.toByte(), 0xDB.toByte(), 0x80.toByte()
        )
    )
    private val frameHfTest32bit3 = IClassFrame(
        byteArrayOf(
            0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x00,
            0x3A, 0xA5.toByte(), 0x18, 0x04, 0x4E, 0x29
        )
    )
    private val frameHfTest37bit = IClassFrame(
        byteArrayOf(
            0x00, 0x12, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x81.toByte(), 0x06, 0x03,
            0x00, 0x03, 0xD0.toByte(), 0x8F.toByte(), 0xE0.toByte(), 0xFD.toByte(), 0xE8.toByte()
        )
    )
    private val frameHfTest37bitWFC = IClassFrame(
        byteArrayOf(
            0x00, 0x12, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x81.toByte(), 0x06, 0x03,
            0x81.toByte(), 0x8A.toByte(), 0x80.toByte(), 0x0B, 0xC0.toByte(), 0xAC.toByte(),
            0xF4.toByte()
        ), true
    )
    private val frameHfTest48bit = IClassFrame(
        byteArrayOf(
            0x00, 0x13, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0B, 0x9E.toByte(), 0x09, 0x81.toByte(), 0x07, 0x00,
            0x80.toByte(), 0x0F, 0xFF.toByte(), 0x03, 0x44, 0x9F.toByte(), 0xE1.toByte(), 0x31
        )
    )
    private val frameHfTest35bit = IClassFrame(
        byteArrayOf(
            0x00, 0x12, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x81.toByte(), 0x06, 0x05,
            0x26, 0x70, 0x01, 0x78, 0xC0.toByte(), 0x57, 0x11
        )
    )
    private val frameLfTest26bit = IClassFrame(
        byteArrayOf(
            0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x80.toByte(), 0x05, 0x06,
            0x3D, 0x80.toByte(), 0x0A, 0x40, 0xA5.toByte(), 0x42
        )
    )
    private val frameLfTest37bit = IClassFrame(
        byteArrayOf(
            0x00, 0x12, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x80.toByte(), 0x06, 0x03,
            0x00, 0x04, 0x4A, 0xA1.toByte(), 0xD8.toByte(), 0x88.toByte(), 0xC0.toByte()
        )
    )
    private val frameLfTest48bit = IClassFrame(
        byteArrayOf(
            0x00, 0x13, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0B, 0x9E.toByte(), 0x09, 0x80.toByte(), 0x07, 0x00,
            0xC0.toByte(), 0x0F, 0xFF.toByte(), 0x03, 0x44, 0xA2.toByte(), 0xAB.toByte(),
            0x94.toByte()
        )
    )

    @Test
    fun getPacs() {
        frameHfTest26bit.pacs.toTypedArray() `should be equal to` byteArrayOf(
            0x00, 0xF6.toByte(), 0x00,
            0x16
        ).toTypedArray()
        frameHfTest26bit.pacsBinaryString `should be equal to` "00000000111101100000000000010110"
        frameHfTest26bit.type `should be equal to` Type.HF
        frameHfTest26bit.cardNumber `should be equal to` 11
        frameHfTest26bit.facilityCode `should be equal to` 123
        frameHfTest26bit.bitLength `should be equal to` 26

        frameHfTest26bit2.pacs.toTypedArray() `should equal` byteArrayOf(
            0x02, 0x02, 0x00,
            0x23
        ).toTypedArray()
        frameHfTest26bit2.pacsBinaryString `should be equal to` "00000010000000100000000000100011"
        frameHfTest26bit2.type `should be equal to` Type.HF
        frameHfTest26bit2.cardNumber `should be equal to` 17
        frameHfTest26bit2.facilityCode `should be equal to` 1
        frameHfTest26bit2.bitLength `should be equal to` 26

        frameHfTest32bit3.pacs.toTypedArray() `should equal` byteArrayOf(
            0x3A, 0xA5.toByte(), 0x18,
            0x04
        ).toTypedArray()
        frameHfTest32bit3.pacsBinaryString `should be equal to` "00111010101001010001100000000100"
        frameHfTest32bit3.type `should be equal to` Type.HF
        frameHfTest32bit3.cardNumber `should be equal to` 35842
        frameHfTest32bit3.facilityCode `should be equal to` 82
        frameHfTest32bit3.bitLength `should be equal to` 32

        frameHfTest37bit.pacs.toTypedArray() `should be equal to` byteArrayOf(
            0x00, 0x00, 0x7A, 0x11,
            0xFC.toByte()
        ).toTypedArray()
        frameHfTest37bit.pacsBinaryString `should be equal to` "0000000000000000011110100001000111111100"
        frameHfTest37bit.type `should be equal to` Type.HF
        frameHfTest37bit.cardNumber `should be equal to` 3999998
        frameHfTest37bit.bitLength `should be equal to` 37

        frameHfTest37bitWFC.pacs.toTypedArray() `should equal` byteArrayOf(
            0x10, 0x31, 0x50, 0x01,
            0x78
        ).toTypedArray()
        frameHfTest37bitWFC.pacsBinaryString `should be equal to` "0001000000110001010100000000000101111000"
        frameHfTest37bitWFC.cardNumber `should be equal to` 188
        frameHfTest37bitWFC.facilityCode `should be equal to` 789
        frameHfTest37bitWFC.bitLength `should be equal to` 37

        frameHfTest48bit.pacs.toTypedArray() `should equal` byteArrayOf(
            0x80.toByte(), 0x0F,
            0xFF.toByte(), 0x03, 0x44, 0x9F.toByte()
        ).toTypedArray()
        frameHfTest48bit.pacsBinaryString `should be equal to` "100000000000111111111111000000110100010010011111"
        frameHfTest48bit.type `should be equal to` Type.HF
        frameHfTest48bit.cardNumber `should be equal to` 107087
        frameHfTest48bit.companyCode `should be equal to` 4095
        frameHfTest48bit.bitLength `should be equal to` 48

        frameHfTest35bit.pacs.toTypedArray() `should equal` byteArrayOf(
            0x01, 0x33, 0x80.toByte(),
            0x0B, 0xC6.toByte()
        ).toTypedArray()
        frameHfTest35bit.pacsBinaryString `should be equal to` "0000000100110011100000000000101111000110"
        frameHfTest35bit.cardNumber `should be equal to` 1507
        frameHfTest35bit.companyCode `should be equal to` 2460 //2496?
        frameHfTest35bit.bitLength `should be equal to` 35 //2496?

        frameLfTest26bit.pacs.toTypedArray() `should equal` byteArrayOf(
            0x00, 0xF6.toByte(), 0x00,
            0x29
        ).toTypedArray()
        frameLfTest26bit.pacsBinaryString `should be equal to` "00000000111101100000000000101001"
        frameLfTest26bit.type `should be equal to` Type.LF
        frameLfTest26bit.cardNumber `should be equal to` 20
        frameLfTest26bit.facilityCode `should be equal to` 123
        frameLfTest26bit.bitLength `should be equal to` 26

        frameLfTest37bit.pacs.toTypedArray() `should be equal to` byteArrayOf(
            0x00, 0x00, 0x89.toByte(),
            0x54, 0x3B
        ).toTypedArray()
        frameLfTest37bit.pacsBinaryString `should be equal to` "0000000000000000100010010101010000111011"
        frameLfTest37bit.type `should be equal to` Type.LF
        frameLfTest37bit.cardNumber `should be equal to` 4499997
        frameLfTest37bit.bitLength `should be equal to` 37

        frameLfTest48bit.pacs.toTypedArray() `should equal` byteArrayOf(
            0xC0.toByte(), 0x0F,
            0xFF.toByte(), 0x03, 0x44, 0xA2.toByte()
        ).toTypedArray()
        frameLfTest48bit.pacsBinaryString `should be equal to` "110000000000111111111111000000110100010010100010"
        frameLfTest48bit.type `should be equal to` Type.LF
        frameLfTest48bit.cardNumber `should be equal to` 107089
        frameLfTest48bit.companyCode `should be equal to` 4095
        frameLfTest48bit.bitLength `should be equal to` 48

        frameMifareTest.pacs.toTypedArray() `should be equal to` byteArrayOf(
            0x83.toByte(), 0x83.toByte(),
            0x9D.toByte(), 0x66
        ).toTypedArray()
        frameMifareTest.pacsBinaryString `should be equal to` "10000011100000111001110101100110"
        frameMifareTest.type `should be equal to` Type.HF
        frameMifareTest.bitLength `should be equal to` 32

        frameMifareCustomMaskTest.pacs.toTypedArray() `should be equal to` byteArrayOf(
           0x00, 0x04.toByte(), 0x91.toByte(),
            0x03, 0xD4.toByte(), 0x37
        ).toTypedArray()

        frameMifareCustomMaskTest.type `should be equal to` Type.HF
        frameMifareCustomMaskTest.cardNumber `should be equal to` 8514075
        frameMifareCustomMaskTest.bitLength `should be equal to` 44

        frameMifare3Test.type `should be equal to` Type.HF
        frameMifare3Test.cardNumber `should be equal to` 2317277
        frameMifare3Test.bitLength `should be equal to` 44
    }

    @Test
    fun toBinaryString() {
        byteArrayOf(
            0x44,
            0x72,
            0xD2.toByte(),
            0x09
        ).toBinaryString() `should be equal to` "01000100011100101101001000001001"
        byteArrayOf(
            0x54,
            0xE2.toByte(),
            0xD1.toByte(),
            0x09
        ).toBinaryString() `should be equal to` "01010100111000101101000100001001"
        byteArrayOf(
            0xD2.toByte(),
            0x75,
            0x55,
            0xF4.toByte()
        ).toBinaryString() `should be equal to` "11010010011101010101010111110100"
        byteArrayOf(
            0xF2.toByte(),
            0xC6.toByte(),
            0x83.toByte(),
            0x76
        ).toBinaryString() `should be equal to` "11110010110001101000001101110110"
        byteArrayOf(
            0x66.toByte(),
            0x9D.toByte(),
            0x83.toByte(),
            0x83.toByte()
        ).toBinaryString() `should be equal to` "01100110100111011000001110000011"

        byteArrayOf(
            0xAA.toByte(), 0x5E.toByte(),
            0x32.toByte(), 0xCE.toByte()
        ).toBinaryString() `should be equal to` "10101010010111100011001011001110"
        byteArrayOf(
            0x87.toByte(), 0x40.toByte(),
            0x01.toByte(), 0x81.toByte()
        ).toBinaryString() `should be equal to` "10000111010000000000000110000001"

    }
}
