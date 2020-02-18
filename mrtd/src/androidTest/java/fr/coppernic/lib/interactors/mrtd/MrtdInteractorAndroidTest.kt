package fr.coppernic.lib.interactors.mrtd

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import fr.coppernic.sdk.passport.lds.Mrz
import fr.coppernic.sdk.power.impl.cone.ConePeripheral
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
        InteractorsDefines.setVerbose(true)
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

    @Test
    fun testGemalto() {
        val mrz = Mrz(MRZ_GEMALTO)
        val dataGroup = interactor.listen(activityRule.activity, mrz.key).blockingGet()
        Timber.d("$dataGroup")
    }

    @Test
    fun testMorpho() {
        val mrz = Mrz(MRZ_MORPHO)
        val dataGroup = interactor.listen(activityRule.activity, mrz.key).blockingGet()
        Timber.d("$dataGroup")
    }
}
