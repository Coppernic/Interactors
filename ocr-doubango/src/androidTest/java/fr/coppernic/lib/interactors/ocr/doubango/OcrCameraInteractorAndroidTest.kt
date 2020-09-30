package fr.coppernic.lib.interactors.ocr.doubango

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.*
import timber.log.Timber

class OcrCameraInteractorAndroidTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<TestActivity> = ActivityScenarioRule(TestActivity::class.java)

    private lateinit var interactor: OcrCameraInteractor
    private lateinit var context: Context

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            InteractorsDefines.verbose = true
            InteractorsDefines.profile = true
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
        interactor = OcrCameraInteractor()
    }

    @After
    fun tearDown() {

    }

    @Test
    fun scanOcr() {
        activityRule.scenario.onActivity { activity ->
            interactor.scan(activity).test().apply {
                awaitTerminalEvent()
                assertComplete()
                assertValueCount(1)
            }
        }
    }

}
