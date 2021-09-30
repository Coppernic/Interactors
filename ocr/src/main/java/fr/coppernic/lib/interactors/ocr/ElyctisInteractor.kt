package fr.coppernic.lib.interactors.ocr

import android.content.Context
import com.elyctis.idboxsdk.mrz.MrzScanner
import fr.coppernic.lib.interactors.ocr.InteractorsDefines.LOG
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

class ElyctisInteractor(private val context: Context) : OcrInteractor {
    private var emitter: ObservableEmitter<String>? = null
    private val mrzListener = object : MrzScanner.Listener {
        override fun onPresenceDetectModeCallback(p0: Byte) {
            if (InteractorsDefines.verbose) {
                LOG.debug("Document detected")
            }
        }

        override fun onContinuousReadModeCallback(s: String) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onContinuousReadModeCallback : {}", s)
            }
            next(s)
        }
    }
    private var mrzReader: MrzScanner = MrzScanner(context, mrzListener)
    private val isReading = AtomicBoolean(false)

    private val observableOnSubscribe = ObservableOnSubscribe<String> { e ->
        setEmitter(e)
        mrzReader = MrzScanner(context, mrzListener)
        if (InteractorsDefines.verbose) {
            LOG.trace("Opening device")
        }
        val ret = mrzReader.open()
        if (!ret) {
            error(OcrInteractorException("Error opening ocr reader"))
        } else {
            if (InteractorsDefines.verbose) {
                LOG.trace("Set continuous read mode")
            }
            mrzReader.setContinuousReadMode(true)
        }
    }

    override fun listen(): Observable<String> {
        return Observable.create(observableOnSubscribe)
    }

    private fun setEmitter(e: ObservableEmitter<String>) {
        LOG.debug(e.toString())

        // End previous observer and start new one
        emitter?.apply {
            if (!isDisposed) {
                onComplete()
                mrzReader.close()
            }
        }
        emitter = e.apply {
            setDisposable(object : Disposable {
                private val disposed = AtomicBoolean(false)

                override fun dispose() {
                    if (InteractorsDefines.verbose) {
                        if (InteractorsDefines.verbose) {
                            LOG.trace("dispose")
                        }
                    }
                    disposed.set(true)
                    mrzReader.close()
                }

                override fun isDisposed(): Boolean {
                    return disposed.get()
                }
            })
        }
    }

    private fun error(t: Throwable) {
        emitter?.let {
            if (!it.isDisposed) {
                it.onError(t)
            }
        }
    }

    private fun next(s: String) {
        emitter?.let {
            if (!it.isDisposed) {
                it.onNext(s)
            }
        }
    }
}
