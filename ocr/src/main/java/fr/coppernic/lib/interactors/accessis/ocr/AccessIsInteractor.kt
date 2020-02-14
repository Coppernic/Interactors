package fr.coppernic.lib.interactors.accessis.ocr

import android.content.Context
import fr.coppernic.lib.interactors.log.InteractorsDefines
import fr.coppernic.lib.interactors.log.InteractorsDefines.LOG
import fr.coppernic.sdk.core.Defines.SerialDefines.OCR_READER_BAUDRATE_CONE
import fr.coppernic.sdk.core.Defines.SerialDefines.OCR_READER_PORT_CONE
import fr.coppernic.sdk.ocr.MrzReader
import fr.coppernic.sdk.utils.io.InstanceListener
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

class AccessIsInteractor(private val context: Context) {
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


    fun listen(): Observable<String> {
        return Observable.create(observableOnSubscribe)
    }

    private fun setEmitter(e: ObservableEmitter<String>) {
        LOG.debug(e.toString())

        // End previous observer and start new one
        emitter?.apply {
            if (!isDisposed) {
                onComplete()
                mrzReader?.close()
            }
        }
        emitter = e.apply {
            setDisposable(object : Disposable {
                private val disposed = AtomicBoolean(false)

                override fun dispose() {
                    if(InteractorsDefines.verbose) {
                        LOG.trace("dispose")
                    }
                    disposed.set(true)
                    mrzReader?.close()
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
            if(InteractorsDefines.verbose) {
                LOG.trace(firmware)
            }
        }

        override fun onMenuData(menu: String) {
            //Display menu data
            if(InteractorsDefines.verbose) {
                LOG.trace(menu)
            }
        }

        override fun onMrz(mrz: String) {
            if(InteractorsDefines.verbose) {
                LOG.trace(mrz)
            }
            emitter?.apply {
                if(!isDisposed){
                    onNext(mrz)
                }
            }
        }
    }

}
