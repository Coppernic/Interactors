package fr.coppernic.lib.interactors.iclass

import androidx.test.core.app.ApplicationProvider
import org.amshove.kluent.`should be`
import org.junit.Test

class IclassInteractorTest {

    @Test
    fun getPACSData() {
        val interactor = IclassInteractor(ApplicationProvider.getApplicationContext())
        val frameTest = byteArrayOf(0x00,0x11, 0x0A, 0x44, 0x00, 0x00, 0x00, 0x00,
                0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x06, 0x3D,
                0x80.toByte(), 0x05, 0x80.toByte(), 0xB4.toByte(), 0x98.toByte())
        interactor.getPACSData(frameTest) `should be` byteArrayOf(0x00, 0xF6.toByte(), 0x00, 0x16)

    }
}