package fr.coppernic.lib.interactors.accessis.ocr

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.hoho.android.usbserial.driver.CdcAcmSerialDriver
import fr.coppernic.lib.interactors.ocr.ElyctisInteractor
import fr.coppernic.lib.interactors.ocr.InteractorsDefines
import fr.coppernic.sdk.power.impl.idplatform.IdPlatformPeripheral
import org.amshove.kluent.shouldBeEqualTo
import org.junit.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


class ElyctisInteractorAndroidTest {

    private lateinit var interactor: ElyctisInteractor
    private lateinit var context: Context

    @Rule
    @JvmField
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant("com.id2mp.permissions.MRZ")

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            Timber.plant(Timber.DebugTree())
            InteractorsDefines.verbose = true
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            Timber.uprootAll()
        }
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Powers on OCR reader
        IdPlatformPeripheral.OCR.descriptor.power(context, true).delay(1, TimeUnit.SECONDS).blockingGet()
        interactor = ElyctisInteractor(context)
    }

    @After
    fun tearDown() {
        // Powers off OCR reader
        IdPlatformPeripheral.OCR.descriptor.power(context, false).blockingGet()
    }


    // Read a passport or ID card to make this test succeed
    @Test
    fun listen() {
        val observer = interactor.listen().test()
        observer.awaitTerminalEvent(10, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.assertValueCount(1)
        observer.dispose()
    }


    // Read a passport or ID card 3 times to make this test succeed
    @Test
    fun retry() {
        val counter = AtomicInteger(0)
        val observer = interactor.listen().doOnSubscribe {
            Timber.v("go go go !!!")
        }.map {
            Timber.v(it)
            counter.incrementAndGet()
            throw Exception()
        }.doOnError {
            Timber.v("Error !!!")
        }.retry { _ ->
            Timber.v("Retry !!!")
            true
        }.test()
        observer.awaitTerminalEvent(30, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.dispose()
        counter.get().shouldBeEqualTo(3)
    }


    // Read a passport or ID card 3 times to make this test succeed
    @Test
    fun retryWithPower() {
        val counter = AtomicInteger(0)
        val observer = IdPlatformPeripheral.OCR.descriptor.power(context, true).doOnSubscribe {
            Timber.v("go go go !!!")
        }.flatMapObservable {
            Timber.v("Power $it")
            interactor.listen()
        }.map {
            Timber.v(it)
            counter.incrementAndGet()
            throw Exception()
        }.doOnError {
            Timber.v("Error !!!")
        }.retry { _ ->
            Timber.v("Retry !!!")
            true
        }.test()
        observer.awaitTerminalEvent(45, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.dispose()
        counter.get().shouldBeEqualTo(3)
    }
}
