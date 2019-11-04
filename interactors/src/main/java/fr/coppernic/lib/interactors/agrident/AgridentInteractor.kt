package fr.coppernic.lib.interactors.agrident

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import fr.coppernic.lib.interactors.ReaderInteractor
import fr.coppernic.sdk.utils.core.CpcDefinitions
import fr.coppernic.sdk.utils.core.CpcResult
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

const val AGRIDENT_WEDGE = "fr.coppernic.tools.cpcagridentwedge"
const val ACTION_READ = "fr.coppernic.tools.agrident.wedge.READ"
const val AGRIDENT_STOP_SERVICE = "fr.coppernic.intent.action.stop.agrident.service"
const val AGRIDENT_START_SERVICE = "fr.coppernic.intent.action.start.agrident.service"

class AgridentInteractor @Inject constructor(private val context: Context) : ReaderInteractor<String> {

    private var emitter: ObservableEmitter<String>? = null
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            processIntent(intent)
        }
    }

    private val observableOnSubscribe = ObservableOnSubscribe<String> { e ->
        setEmitter(e)
        registerReceiver()
    }

    override fun trig() {
        // Starts Agrident wedge
        val scanIntent = Intent()
        scanIntent.setPackage(AGRIDENT_WEDGE)
        scanIntent.action = ACTION_READ
        scanIntent.putExtra(CpcDefinitions.KEY_PACKAGE, context.packageName)
        val info = context.startService(scanIntent)
        if (info == null) {
            handleError(CpcResult.ResultException(CpcResult.RESULT.SERVICE_NOT_FOUND))
        }
    }

    override fun listen(): Observable<String> {
        return Observable.create(observableOnSubscribe)
    }

    private fun setEmitter(e: ObservableEmitter<String>) {
        Timber.d(e.toString())

        // End previous observer and start new one
        emitter?.apply {
            if(!isDisposed) {
                onComplete()
            }
        }

        emitter = e.apply {
            setDisposable(object : Disposable {
                private val disposed = AtomicBoolean(false)

                override fun dispose() {
                    Timber.d("unregister")
                    unregisterReceiver()
                    disposed.set(true)
                }

                override fun isDisposed(): Boolean {
                    return disposed.get()
                }
            })
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(CpcDefinitions.ACTION_AGRIDENT_SUCCESS)
        filter.addAction(CpcDefinitions.ACTION_AGRIDENT_ERROR)
        context.registerReceiver(receiver, filter)
    }

    private fun unregisterReceiver() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            Timber.v(e.toString())
        }
    }

    private fun processIntent(intent: Intent) {
        val action = intent.action
        if (action == null) {
            Timber.e("Action of %s is null", intent)
            return
        }

        val localEmitter = if (emitter == null) {
            unregisterReceiver()
            return
        } else {
            emitter!!
        }

        if (action == CpcDefinitions.ACTION_AGRIDENT_SUCCESS) {
            val extras = intent.extras
            if (extras == null) {
                Timber.e("No extras for ACTION_AGRIDENT_SUCCESS")
                return
            }
            val data = extras.getString(CpcDefinitions.KEY_BARCODE_DATA, "")
            localEmitter.onNext(data)
        } else if (intent.action == CpcDefinitions.ACTION_AGRIDENT_ERROR) {
            val res = intent.getIntExtra(CpcDefinitions.KEY_RESULT, CpcResult.RESULT.ERROR.ordinal)
            val result = CpcResult.RESULT.values()[res]
            handleError(result.toException())
        }
    }

    override fun startService() {
        val scanIntent = Intent()
        scanIntent.setPackage(AGRIDENT_WEDGE)
        scanIntent.action = AGRIDENT_START_SERVICE
        scanIntent.putExtra(CpcDefinitions.KEY_PACKAGE, context.packageName)
        context.startService(scanIntent)
    }

    override fun stopService() {
        val scanIntent = Intent()
        scanIntent.setPackage(AGRIDENT_WEDGE)
        scanIntent.action = AGRIDENT_STOP_SERVICE
        scanIntent.putExtra(CpcDefinitions.KEY_PACKAGE, context.packageName)
        context.startService(scanIntent)
    }

    private fun handleError(t: Throwable) {
        unregisterReceiver()
        emitter?.apply {
            if (!isDisposed) {
                onError(t)
            }
        }
    }
}
