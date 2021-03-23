package fr.coppernic.lib.interactors.iclass

import org.amshove.kluent.`should equal`
import org.junit.Test

class IClassFrameTest {
    private val frameMifareTest = IClassFrame(byteArrayOf(0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(),0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x00,
            0x83.toByte(), 0x83.toByte(), 0x9D.toByte(), 0x66, 0x33, 0xD6.toByte()))
    private val frameHfTest26bit = IClassFrame(byteArrayOf(0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x06,
            0x3D, 0x80.toByte(), 0x05, 0x80.toByte(), 0xB4.toByte(), 0x98.toByte()))
    private val frameHfTest26bit2 = IClassFrame(byteArrayOf(0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x06,
            0x80.toByte(), 0x80.toByte(), 0x08, 0xC0.toByte(), 0xDB.toByte(), 0x80.toByte()))
    private val frameHfTest26bit3 = IClassFrame(byteArrayOf(0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x00,
            0x3A, 0xA5.toByte(), 0x18, 0x04, 0x4E, 0x29))
    private val frameHfTest37bit = IClassFrame(byteArrayOf(0x00, 0x12, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x81.toByte(), 0x06, 0x03,
            0x00, 0x03, 0xD0.toByte(), 0x8F.toByte(), 0xE0.toByte(), 0xFD.toByte(), 0xE8.toByte()))
    private val frameHfTest37bitWFC = IClassFrame(byteArrayOf(0x00, 0x12, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x81.toByte(), 0x06, 0x03,
            0x81.toByte(), 0x8A.toByte(), 0x80.toByte(), 0x0B, 0xC0.toByte(), 0xAC.toByte(),
            0xF4.toByte()), true)
    private val frameHfTest48bit = IClassFrame(byteArrayOf(0x00, 0x13, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0B, 0x9E.toByte(), 0x09, 0x81.toByte(), 0x07, 0x00,
            0x80.toByte(), 0x0F, 0xFF.toByte(), 0x03, 0x44, 0x9F.toByte(), 0xE1.toByte(), 0x31))
    private val frameHfTest35bit = IClassFrame(byteArrayOf(0x00, 0x12, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x81.toByte(), 0x06, 0x05,
            0x26, 0x70, 0x01, 0x78, 0xC0.toByte(), 0x57, 0x11))
    private val frameLfTest26bit = IClassFrame(byteArrayOf(0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x80.toByte(), 0x05, 0x06,
            0x3D, 0x80.toByte(), 0x0A, 0x40, 0xA5.toByte(), 0x42))
    private val frameLfTest37bit = IClassFrame(byteArrayOf(0x00, 0x12, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x80.toByte(), 0x06, 0x03,
            0x00, 0x04, 0x4A, 0xA1.toByte(), 0xD8.toByte(), 0x88.toByte(), 0xC0.toByte()))
    private val frameLfTest48bit = IClassFrame(byteArrayOf(0x00, 0x13, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(),0x0B, 0x9E.toByte(), 0x09, 0x80.toByte(), 0x07, 0x00,
            0xC0.toByte(), 0x0F, 0xFF.toByte(), 0x03, 0x44, 0xA2.toByte(), 0xAB.toByte(),
            0x94.toByte()))

    @Test
    fun getPacs() {
        frameHfTest26bit.pacs.toTypedArray() `should equal` byteArrayOf(0x00, 0xF6.toByte(), 0x00,
                0x16).toTypedArray()
        frameHfTest26bit.pacsBinaryString `should equal` "00000000111101100000000000010110"
        frameHfTest26bit.type `should equal` Type.HF
        frameHfTest26bit.cardNumber `should equal` 11
        frameHfTest26bit.facilityCode `should equal` 123
        frameHfTest26bit.bitLength `should equal` 26

        frameHfTest26bit2.pacs.toTypedArray() `should equal` byteArrayOf(0x02, 0x02, 0x00,
                0x23).toTypedArray()
        frameHfTest26bit2.pacsBinaryString `should equal` "00000010000000100000000000100011"
        frameHfTest26bit2.type `should equal` Type.HF
        frameHfTest26bit2.cardNumber `should equal` 17
        frameHfTest26bit2.facilityCode `should equal` 1
        frameHfTest26bit2.bitLength `should equal` 26

        frameHfTest26bit3.pacs.toTypedArray() `should equal` byteArrayOf(0x3A, 0xA5.toByte(), 0x18,
                0x04).toTypedArray()
        frameHfTest26bit3.pacsBinaryString `should equal` "00111010101001010001100000000100"
        frameHfTest26bit3.type `should equal` Type.HF
        frameHfTest26bit3.cardNumber `should equal` 35842
        frameHfTest26bit3.facilityCode `should equal` 82
//        frameHfTest26bit3.bitLength `should equal` 26

        frameHfTest37bit.pacs.toTypedArray() `should equal` byteArrayOf(0x00, 0x00, 0x7A, 0x11,
                0xFC.toByte()).toTypedArray()
        frameHfTest37bit.pacsBinaryString `should equal` "0000000000000000011110100001000111111100"
        frameHfTest37bit.type `should equal` Type.HF
        frameHfTest37bit.cardNumber `should equal` 3999998
        frameHfTest37bit.bitLength `should equal` 37

        frameHfTest37bitWFC.pacs.toTypedArray() `should equal` byteArrayOf( 0x10, 0x31, 0x50, 0x01,
                0x78).toTypedArray()
        frameHfTest37bitWFC.pacsBinaryString `should equal` "0001000000110001010100000000000101111000"
        frameHfTest37bitWFC.cardNumber `should equal` 188
        frameHfTest37bitWFC.facilityCode `should equal` 789
        frameHfTest37bitWFC.bitLength `should equal` 37

        frameHfTest48bit.pacs.toTypedArray() `should equal` byteArrayOf(0x80.toByte(), 0x0F,
                0xFF.toByte(), 0x03, 0x44, 0x9F.toByte()).toTypedArray()
        frameHfTest48bit.pacsBinaryString `should equal` "100000000000111111111111000000110100010010011111"
        frameHfTest48bit.type `should equal` Type.HF
        frameHfTest48bit.cardNumber `should equal` 107087
        frameHfTest48bit.companyCode `should equal` 4095
        frameHfTest48bit.bitLength `should equal` 48

        frameHfTest35bit.pacs.toTypedArray() `should equal` byteArrayOf(0x01, 0x33, 0x80.toByte(),
                0x0B, 0xC6.toByte()).toTypedArray()
        frameHfTest35bit.pacsBinaryString `should equal` "0000000100110011100000000000101111000110"
        frameHfTest35bit.cardNumber `should equal` 1507
        frameHfTest35bit.companyCode `should equal` 2460 //2496?
        frameHfTest35bit.bitLength `should equal` 35 //2496?

        frameLfTest26bit.pacs.toTypedArray() `should equal` byteArrayOf(0x00, 0xF6.toByte(), 0x00,
                0x29).toTypedArray()
        frameLfTest26bit.pacsBinaryString `should equal` "00000000111101100000000000101001"
        frameLfTest26bit.type `should equal` Type.LF
        frameLfTest26bit.cardNumber `should equal` 20
        frameLfTest26bit.facilityCode `should equal` 123
        frameLfTest26bit.bitLength `should equal` 26

        frameLfTest37bit.pacs.toTypedArray() `should equal` byteArrayOf(0x00, 0x00,0x89.toByte(),
                0x54, 0x3B).toTypedArray()
        frameLfTest37bit.pacsBinaryString `should equal` "0000000000000000100010010101010000111011"
        frameLfTest37bit.type `should equal` Type.LF
        frameLfTest37bit.cardNumber `should equal` 4499997
        frameLfTest37bit.bitLength `should equal` 37

        frameLfTest48bit.pacs.toTypedArray() `should equal` byteArrayOf(0xC0.toByte(), 0x0F,
                0xFF.toByte(), 0x03, 0x44, 0xA2.toByte()).toTypedArray()
        frameLfTest48bit.pacsBinaryString `should equal` "110000000000111111111111000000110100010010100010"
        frameLfTest48bit.type `should equal` Type.LF
        frameLfTest48bit.cardNumber `should equal` 107089
        frameLfTest48bit.companyCode `should equal` 4095
        frameLfTest48bit.bitLength `should equal` 48

        frameMifareTest.pacs.toTypedArray() `should equal` byteArrayOf( 0x83.toByte(), 0x83.toByte(),
                0x9D.toByte(), 0x66).toTypedArray()
        frameMifareTest.pacsBinaryString `should equal` "10000011100000111001110101100110"
        frameMifareTest.type `should equal` Type.HF
        frameMifareTest.bitLength `should equal` 32
    }
}
