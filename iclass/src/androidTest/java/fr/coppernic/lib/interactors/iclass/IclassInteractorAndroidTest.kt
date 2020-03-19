package fr.coppernic.lib.interactors.iclass

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import fr.coppernic.sdk.power.impl.cone.ConePeripheral
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IclassInteractorAndroidTest {

    private lateinit var interactor: IclassInteractor
    private lateinit var context: Context

    @Before
    fun setUp() {
        InteractorsDefines.setVerbose(true)
        context = ApplicationProvider.getApplicationContext()
        // Powers on OCR reader
        ConePeripheral.RFID_HID_ICLASSPROX_GPIO.descriptor.power(context, true).blockingGet()
        interactor = IclassInteractor(context)
    }

    @After
    fun tearDown() {
        ConePeripheral.RFID_HID_ICLASSPROX_GPIO.descriptor.power(context, false).blockingGet()
    }

    @Test
    fun listen() {
        val testObserver = interactor.listen().test()
        SystemClock.sleep(10000)
        testObserver.awaitCount(1) //Timeout after 5 seconds
        testObserver.assertValueCount(1)
    }

    @Test
    fun severalListen() {
        val testObserver = interactor.listen().test()
        val testObserver2 = interactor.listen().test()
        testObserver2.awaitCount(1) //Timeout after 5 seconds
        testObserver2.assertValueCount(1)
        testObserver.assertValueCount(0)
    }

    @Test
    fun getPACSData() {
        val frameTest = byteArrayOf(0x00, 0x11, 0x0A, 0x44, 0x00, 0x00, 0x00, 0x00,
                0xBD.toByte(), 0x09, 0x9E.toByte(), 0x07, 0x81.toByte(), 0x05, 0x06, 0x3D,
                0x80.toByte(), 0x05, 0x80.toByte(), 0xB4.toByte(), 0x98.toByte())
        val frameTest37bit = byteArrayOf(0x00, 0x12, 0x0A, 0x44, 0x00, 0x00, 0x00, 0x00,
                0xBD.toByte(), 0x0A, 0x9E.toByte(), 0x08, 0x81.toByte(), 0x06, 0x03, 0x00, 0x03,
                0xD0.toByte(), 0x8F.toByte(), 0xE0.toByte(), 0xFD.toByte(), 0xE8.toByte())
        val frameTest48bit = byteArrayOf(0x00, 0x12, 0x0A, 0x44, 0x00, 0x00, 0x00, 0x00,
                0xBD.toByte(), 0x0B, 0x9E.toByte(), 0x09, 0x81.toByte(), 0x07, 0x00, 0x80.toByte(),
                0x0F, 0xFF.toByte(), 0x03, 0x44, 0x9F.toByte(), 0xE1.toByte(), 0x31)

        Assert.assertArrayEquals(interactor.getPACSData(frameTest), byteArrayOf(0x00, 0xF6.toByte(),
                0x00, 0x16))
        Assert.assertArrayEquals(interactor.getPACSData(frameTest37bit), byteArrayOf(0x00, 0x00,
                0x00, 0x00, 0x00, 0x7A, 0x11, 0xFC.toByte()))
        Assert.assertArrayEquals(interactor.getPACSData(frameTest48bit), byteArrayOf(0x00, 0x00,
                0x80.toByte(), 0x0F, 0xFF.toByte(), 0x03, 0x44, 0x9F.toByte()))
    }

}
