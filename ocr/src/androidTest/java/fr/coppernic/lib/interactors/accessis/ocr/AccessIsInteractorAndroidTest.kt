package fr.coppernic.lib.interactors.accessis.ocr

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import fr.coppernic.lib.interactors.ocr.AccessIsInteractor
import fr.coppernic.sdk.power.impl.cone.ConePeripheral
import org.amshove.kluent.shouldBeEqualTo
import org.junit.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class AccessIsInteractorAndroidTest {

    private lateinit var interactor: AccessIsInteractor
    private lateinit var context: Context

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            Timber.plant(Timber.DebugTree())
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
        ConePeripheral.OCR_ACCESSIS_AI310E_USB.descriptor.power(context, true).blockingGet()
        interactor = AccessIsInteractor(context)
    }

    @After
    fun tearDown() {
        // Powers off OCR reader
        ConePeripheral.OCR_ACCESSIS_AI310E_USB.descriptor.power(context, false).blockingGet()
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
        observer.awaitTerminalEvent(15, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.dispose()
        counter.get().shouldBeEqualTo(3)
    }


    // Read a passport or ID card 3 times to make this test succeed
    @Test
    fun retryWithPower() {
        val counter = AtomicInteger(0)
        val observer = ConePeripheral.OCR_ACCESSIS_AI310E_USB.descriptor.power(context, true).doOnSubscribe {
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
        observer.awaitTerminalEvent(30, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.dispose()
        counter.get().shouldBeEqualTo(3)
    }
}
