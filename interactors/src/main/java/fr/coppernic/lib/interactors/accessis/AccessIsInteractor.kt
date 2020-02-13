package fr.coppernic.lib.interactors.accessis

import android.content.Context
import fr.coppernic.lib.interactors.ReaderInteractor
import fr.coppernic.lib.interactors.common.InteractorsDefines
import fr.coppernic.sdk.core.Defines.SerialDefines.OCR_READER_BAUDRATE_CONE
import fr.coppernic.sdk.core.Defines.SerialDefines.OCR_READER_PORT_CONE
import fr.coppernic.sdk.ocr.MrzReader
import fr.coppernic.sdk.power.impl.cone.ConePeripheral
import fr.coppernic.sdk.utils.io.InstanceListener
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

class AccessIsInteractor(val context: Context) : ReaderInteractor<String> {
    private var emitter: ObservableEmitter<String>? = null
    private var mrzReader: MrzReader? = null

    private val observableOnSubscribe = ObservableOnSubscribe<String> { e ->
        setEmitter(e)
        MrzReader.Builder.get()
                .setListener(mrzListener)
                .withPort(OCR_READER_PORT_CONE)
                .withBaudrate(OCR_READER_BAUDRATE_CONE)
                .build(context, instanceListener)
    }

    private val instanceListener = object : InstanceListener<MrzReader> {
        override fun onDisposed(p0: MrzReader?) {
        }

        override fun onCreated(reader: MrzReader?) {
            mrzReader = reader
            mrzReader?.open()
        }
    }

    override fun trig() {
    }

    override fun listen(): Observable<String> {
        return Observable.create(observableOnSubscribe)
    }

    private fun setEmitter(e: ObservableEmitter<String>) {
        InteractorsDefines.LOG.debug(e.toString())

        // End previous observer and start new one
        emitter?.apply {
            if (!isDisposed) {
                onComplete()
                mrzReader?.close()
            }
        }
        // Powers on OCR reader
        ConePeripheral.OCR_ACCESSIS_AI310E_USB.on(context)
        emitter = e.apply {
            setDisposable(object : Disposable {
                private val disposed = AtomicBoolean(false)

                override fun dispose() {
                    InteractorsDefines.LOG.debug("dispose")
                    disposed.set(true)
                    mrzReader?.close()
                    // Powers off OCR reader
                    ConePeripheral.OCR_ACCESSIS_AI310E_USB.off(context)
                }

                override fun isDisposed(): Boolean {
                    return disposed.get()
                }
            })
        }
    }

    private val mrzListener = object : MrzReader.Listener {
        override fun onFirmware(firmware: String) {
            //Display Firmware version
        }

        override fun onMenuData(menu: String) {
            //Display menu data
        }

        override fun onMrz(mrz: String) {
            emitter?.onNext(mrz)
        }
    }

    override fun stopService() {
    }

    override fun startService() {
    }

}
