package fr.coppernic.lib.interactors.accessis

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
        interactor = AccessIsInteractor(context)
    }

    @After
    fun tearDown() {
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
