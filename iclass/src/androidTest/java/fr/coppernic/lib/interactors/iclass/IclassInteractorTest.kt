package fr.coppernic.lib.interactors.iclass

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import fr.coppernic.sdk.power.impl.cone.ConePeripheral
import org.junit.After
import org.junit.Before
import org.junit.Test

class IclassInteractorTest {

    private lateinit var interactor: IclassInteractor
    private lateinit var context: Context

    @Before
    fun setUp() {
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
}
