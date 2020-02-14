package fr.coppernic.lib.interactors.accessis.ocr

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import fr.coppernic.sdk.power.impl.cone.ConePeripheral
import org.junit.After
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AccessIsInteractorAndroidTest {

    private lateinit var interactor: AccessIsInteractor
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Powers on OCR reader
        ConePeripheral.OCR_ACCESSIS_AI310E_USB.on(context)
        interactor = AccessIsInteractor(context)
    }

    @After
    fun tearDown() {
        // Powers off OCR reader
        ConePeripheral.OCR_ACCESSIS_AI310E_USB.off(context)
    }


    // Read a passport or ID card to make this test succeed
    @Test
    fun listen() {
        Timber.d("listen")
        val observer = interactor.listen().test()
        observer.awaitTerminalEvent(10, TimeUnit.SECONDS)
        observer.assertNoErrors()
        observer.assertValueCount(1)
        observer.dispose()
    }
}
