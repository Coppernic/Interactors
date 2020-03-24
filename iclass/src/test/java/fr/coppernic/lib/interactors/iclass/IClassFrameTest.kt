package fr.coppernic.lib.interactors.iclass

import org.amshove.kluent.`should equal`
import org.junit.Test

class IClassFrameTest {

    private val frameHfTest26bit = IClassFrame(byteArrayOf(0x00, 0x11, 0x0A, 0x44, 0x00,
            0x00, 0x00, 0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x06,
            0x3D, 0x80.toByte(), 0x05, 0x80.toByte(), 0xB4.toByte(), 0x98.toByte()))
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
        frameHfTest26bit.pacs?.toTypedArray() `should equal` byteArrayOf(0x00, 0xF6.toByte(), 0x00,
                0x16).toTypedArray()
        frameHfTest26bit.type `should equal` Type.HF
        frameHfTest26bit.cardNumber `should equal` 11
        frameHfTest26bit.facilityCode `should equal` 123

        frameHfTest37bit.pacs?.toTypedArray() `should equal` byteArrayOf(0x00, 0x00, 0x7A, 0x11,
                0xFC.toByte()).toTypedArray()
        frameHfTest37bit.type `should equal` Type.HF
        frameHfTest37bit.cardNumber `should equal` 3999998

        frameHfTest37bitWFC.pacs?.toTypedArray() `should equal` byteArrayOf( 0x10, 0x31, 0x50, 0x01,
                0x78).toTypedArray()
        frameHfTest37bitWFC.cardNumber `should equal` 188
        frameHfTest37bitWFC.facilityCode `should equal` 789

        frameHfTest48bit.pacs?.toTypedArray() `should equal` byteArrayOf(0x80.toByte(), 0x0F,
                0xFF.toByte(), 0x03, 0x44, 0x9F.toByte()).toTypedArray()
        frameHfTest48bit.type `should equal` Type.HF
        frameHfTest48bit.cardNumber `should equal` 107087
        frameHfTest48bit.companyCode `should equal` 4095

        frameHfTest35bit.pacs?.toTypedArray() `should equal` byteArrayOf(0x01, 0x33, 0x80.toByte(),
                0x0B, 0xC6.toByte()).toTypedArray()
        frameHfTest35bit.cardNumber `should equal` 1507
        frameHfTest35bit.companyCode `should equal` 2460 //2496?

        frameLfTest26bit.pacs?.toTypedArray() `should equal` byteArrayOf(0x00, 0xF6.toByte(), 0x00,
                0x29).toTypedArray()
        frameLfTest26bit.type `should equal` Type.LF
        frameLfTest26bit.cardNumber `should equal` 20
        frameLfTest26bit.facilityCode `should equal` 123

        frameLfTest37bit.pacs?.toTypedArray() `should equal` byteArrayOf(0x00, 0x00,0x89.toByte(),
                0x54, 0x3B).toTypedArray()
        frameLfTest37bit.type `should equal` Type.LF
        frameLfTest37bit.cardNumber `should equal` 4499997

        frameLfTest48bit.pacs?.toTypedArray() `should equal` byteArrayOf(0xC0.toByte(), 0x0F,
                0xFF.toByte(), 0x03, 0x44, 0xA2.toByte()).toTypedArray()
        frameLfTest48bit.type `should equal` Type.LF
        frameLfTest48bit.cardNumber `should equal` 107089
        frameLfTest48bit.companyCode `should equal` 4095
    }
}
