package fr.coppernic.lib.interactors.uhf

import android.content.Context
import com.caen.RFIDLibrary.CAENRFIDReader
import fr.coppernic.lib.interactors.ReaderInteractor
import fr.coppernic.sdk.utils.core.CpcResult
import fr.coppernic.sdk.utils.debug.L
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Action
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton

const val CAEN_READER_PORT = "/dev/ttyHSL1"
const val TIMEOUT = 3000L
const val TAG = "CaenInteractor"
const val DEBUG = true

@Singleton
class CaenInteractor<T> : ReaderInteractor<T>{
    @Inject
    lateinit var context: Context

    private val scheduler by lazy { Schedulers.from(Executors.newSingleThreadExecutor()) }
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private lateinit var reader: CAENRFIDReader

    private var subject: Subject<T>? = null


    override fun trig() {
        L.mt(TAG, DEBUG)

        if (subject?.hasComplete() == false) {
            if (!::reader.isInitialized) {
                waitingForReaderToBeReady()
            }
            scheduler.scheduleDirect { read() }
        } else {
            Timber.e("No subject available to notify error")
        }
    }

    override fun listen(): Observable<T> {
        if (subject == null) {
            Timber.v("Create subject")
            subject = PublishSubject.create<T>()
        }

        return if (::reader.isInitialized) {
            Timber.v("Reader is not null, return subject")
            subject!!
        } else {
            Timber.v("Return observable that init reader")
            initReader().toObservable()
                    .flatMap(Function<CpcResult.RESULT, ObservableSource<String>> { subject })
                    .doOnDispose(Action {
                        subject = null
                        setReader(null)
                    })
        }
    }

    private fun sendData(data: T) {
        if (subject?.hasComplete() == false) {
            subject.onNext(data)
        }
    }

    private fun sendError(res: CpcResult.RESULT, format: String, vararg objects: Any) {
        Timber.e(format, *objects)
        if (subject != null && !subject.hasComplete()) {
            subject.onError(res.toException())
            subject = null
        }
    }

    private fun sendError(t: Throwable) {
        Timber.e(t)
        if (subject != null && !subject.hasComplete()) {
            subject.onError(t)
            subject = null
        }
    }

    private fun waitingForReaderToBeReady(){
        lock.lock()
        try {
            // Waiting for reader to be ready
            condition.await(TIMEOUT, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        finally {
            lock.unlock()
        }
    }

    private fun read() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}