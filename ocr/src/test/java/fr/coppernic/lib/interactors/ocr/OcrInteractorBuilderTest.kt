package fr.coppernic.lib.interactors.ocr

import androidx.test.core.app.ApplicationProvider
import fr.coppernic.test.robolectric.RobolectricTest
import org.junit.Test
import timber.log.Timber

class OcrInteractorBuilderTest : RobolectricTest() {
    @Test(expected = OcrInteractorException::class)
    fun build() {
        val ocr = OcrInteractorBuilder.build(ApplicationProvider.getApplicationContext())
        Timber.v("Class : ${ocr.javaClass.name}")
    }
}
