package fr.coppernic.lib.interactors.iclass

import org.amshove.kluent.`should equal`
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class IclassFrameDecoderTest {

    private val frameHfTest26bit = IclassFrameDecoder(byteArrayOf(0x00, 0x11, 0x0A, 0x44, 0x00, 0x00, 0x00, 0x00,
            0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x06, 0x3D,
            0x80.toByte(), 0x05, 0x80.toByte(), 0xB4.toByte(), 0x98.toByte()))
    private val frameTest37bit = IclassFrameDecoder(byteArrayOf(0x00, 0x12, 0x0A, 0x44, 0x00, 0x00, 0x00, 0x00,
            0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x81.toByte(), 0x06, 0x03, 0x00, 0x03,
            0xD0.toByte(), 0x8F.toByte(), 0xE0.toByte(), 0xFD.toByte(), 0xE8.toByte()))
    private val frameTest48bit = IclassFrameDecoder(byteArrayOf(0x00, 0x12, 0x0A, 0x44, 0x00, 0x00, 0x00, 0x00,
            0xBD.toByte(), 0x0B, 0x9E.toByte(), 0x09, 0x81.toByte(), 0x07, 0x00, 0x80.toByte(),
            0x0F, 0xFF.toByte(), 0x03, 0x44, 0x9F.toByte(), 0xE1.toByte(), 0x31))
    private val frameLfTest26bit = IclassFrameDecoder(byteArrayOf(0x00, 0x11, 0x0A, 0x44, 0x00, 0x00, 0x00,
            0x00, 0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x80.toByte(), 0x05, 0x06, 0x3D,
            0x80.toByte(), 0x0A, 0x40, 0xA5.toByte(), 0x42))
    private val frameLfTest37bit = IclassFrameDecoder(byteArrayOf(0x00, 0x12, 0x0A, 0x44, 0x00, 0x00, 0x00,
            0x00, 0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x80.toByte(), 0x06, 0x03, 0x00, 0x04, 0x4A,
            0xA1.toByte(), 0xD8.toByte(), 0x88.toByte(), 0xC0.toByte()))
    private val frameLfTest48bit = IclassFrameDecoder(byteArrayOf(0x00, 0x13, 0x0A, 0x44, 0x00, 0x00, 0x00,
            0x00, 0xBD.toByte(),0x0B, 0x9E.toByte(), 0x09, 0x80.toByte(), 0x07, 0x00, 0xC0.toByte(),
            0x0F, 0xFF.toByte(), 0x03, 0x44, 0xA2.toByte(), 0xAB.toByte(), 0x94.toByte()))

    @Test
    fun getPacs() {
        assertArrayEquals(frameHfTest26bit.pacs, byteArrayOf(0x00, 0xF6.toByte(),
                0x00, 0x16))
        assertArrayEquals(frameTest37bit.pacs, byteArrayOf(0x00, 0x00, 0x7A, 0x11, 0xFC.toByte()))
        assertArrayEquals(frameTest48bit.pacs, byteArrayOf(0x80.toByte(), 0x0F, 0xFF.toByte(), 0x03, 0x44,
                0x9F.toByte()))


        frameHfTest26bit.pacs?.toTypedArray() `should equal` byteArrayOf(0x00, 0xF6.toByte(), 0x00, 0x16).toTypedArray()
        frameHfTest26bit.type `should equal` Type.HF
        frameHfTest26bit.cardNumber `should equal` 11
        frameHfTest26bit.facilityCode `should equal` 123

        frameTest37bit.type `should equal` Type.HF
        frameTest37bit.cardNumber `should equal` 3999998

        frameTest48bit.type `should equal` Type.HF
        frameTest48bit.cardNumber `should equal` 107087
        frameTest48bit.companyCode `should equal` 4095

        frameLfTest26bit.pacs?.toTypedArray() `should equal` byteArrayOf(0x00, 0xF6.toByte(), 0x00, 0x29).toTypedArray()
        frameLfTest26bit.type `should equal` Type.LF
        frameLfTest26bit.cardNumber `should equal` 20
        frameLfTest26bit.facilityCode `should equal` 123

        frameLfTest37bit.pacs?.toTypedArray() `should equal` byteArrayOf(0x00, 0x00,0x89.toByte(), 0x54, 0x3B).toTypedArray()
        frameLfTest37bit.type `should equal` Type.LF
        frameLfTest37bit.cardNumber `should equal` 4499997

        frameLfTest48bit.pacs?.toTypedArray() `should equal` byteArrayOf(0xC0.toByte(), 0x0F,0xFF.toByte(), 0x03, 0x44, 0xA2.toByte()).toTypedArray()
        frameLfTest48bit.type `should equal` Type.LF
        frameLfTest48bit.cardNumber `should equal` 107089
        frameLfTest48bit.companyCode `should equal` 4095
    }
}
