package fr.coppernic.lib.interactors.mrtd

import android.os.SystemClock
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import fr.coppernic.sdk.passport.lds.Mrz
import fr.coppernic.sdk.power.impl.cone.ConePeripheral
import io.reactivex.observers.TestObserver
import java.util.concurrent.TimeUnit
import org.junit.*
import timber.log.Timber

const val MRZ_GEMALTO = "P<UTOTRAVELLER<<JANE<<<<<<<<<<<<<<<<<<<<<<<<00000000<0UTO7804115F131201211041978<<<<<<32"
const val MRZ_MORPHO = "P<FRAPIEPERS<<MARC<<<<<<<<<<<<<<<<<<<<<<<<<<0000000228FRA7511080M1111053000022<<<<<<<<82"
const val MRZ_BENIN = "P<BENECHANTILLONB<<SAMPLEB<<<<<<<<<<<<<<<<<<B0573327<8BEN8108315F2210171000548706<<<<<06"
const val MRZ_SECURITY_SYSTEM = "P<UTODOE<<JANE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2424110103D<<6908083F1208024<<<<<<<<<<<<<<<8"

class MrtdInteractorAndroidTest {

    @get:Rule
    var activityRule: ActivityTestRule<TestActivity> =
            ActivityTestRule(TestActivity::class.java)

    private lateinit var interactor: MrtdInteractor

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
        InteractorsMrtdDefines.setVerbose(true)
        interactor = MrtdInteractor()

        Timber.v("Powering up")
        ConePeripheral.RFID_ELYCTIS_LF214_USB.descriptor.power(InstrumentationRegistry.getInstrumentation().targetContext, true)
                .blockingGet()
        Timber.v("Powered up")
    }

    @After
    fun after() {
        Timber.v("Powering down")
        ConePeripheral.RFID_ELYCTIS_LF214_USB.descriptor.power(InstrumentationRegistry.getInstrumentation().targetContext, false)
                .blockingGet()
        Timber.v("Powered down")
    }

    private fun testMrtdGeneric(mrzStr: String) {
        val mrz = Mrz(mrzStr)
        val testObserver = TestObserver<MrtdInteractorState>()
        interactor.listen(activityRule.activity, mrz.key)
            .subscribe(testObserver)

        testObserver.awaitTerminalEvent(10, TimeUnit.SECONDS)
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        val mrtdReadDoneList = testObserver.values().filter { it is MrtdInteractorState.MrtdReadDone }
        assert(mrtdReadDoneList.isNotEmpty())
        val mrtdReadDone = mrtdReadDoneList[0] as MrtdInteractorState.MrtdReadDone

        Timber.d("mrtdReadDone.dataGroup=%s", mrtdReadDone.dataGroup.toString())
    }

    @Test
    fun testGemalto() {
        testMrtdGeneric(MRZ_GEMALTO)
    }

    @Test
    fun testMorpho() {
        testMrtdGeneric(MRZ_MORPHO)
    }

    @Test
    fun busy() {
        val mrz = Mrz(MRZ_BENIN)
        val obs1 = interactor.listen(activityRule.activity, mrz.key, MrtdInteractor.Options(timeout = 5000)).test()
        val obs2 = interactor.listen(activityRule.activity, mrz.key, MrtdInteractor.Options(timeout = 5000)).test()
        obs1.awaitTerminalEvent()
        obs2.awaitTerminalEvent()
        obs2.assertError(MrtdInteractorException::class.java)
        obs1.assertError(MrtdInteractorException::class.java)
        obs2.assertErrorMessage("Busy")
        obs1.assertErrorMessage("onBacEnded error: CCID_INVALID_ANSWER")
    }

    @Test
    fun dispose() {
        val mrz = Mrz(MRZ_SECURITY_SYSTEM)
        val obs1 = interactor.listen(activityRule.activity, mrz.key, MrtdInteractor.Options(timeout = 5000)).test()
        SystemClock.sleep(1000)
        obs1.dispose()
        obs1.assertNoErrors()

        Timber.i("Listen an other device")

        /*val obs2 = interactor.listen(activityRule.activity, mrz.key, MrtdInteractor.Options(timeout = 2000)).test()
        obs2.awaitTerminalEvent()
        obs2.assertError(MrtdInteractorException::class.java)
        obs2.assertErrorMessage("onBacEnded error: CCID_INVALID_ANSWER")*/

        SystemClock.sleep(2000)
    }
}
