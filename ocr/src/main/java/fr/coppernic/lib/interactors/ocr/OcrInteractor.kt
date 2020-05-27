package fr.coppernic.lib.interactors.ocr

import io.reactivex.Observable

interface OcrInteractor {
    /**
     * Listening for MRZ string.
     *
     * <p>
     * Call Observable.subscribe to start reading.
     * Get Disposable object from RxJava Observable to dispose reading when done. Do this to avoid resource leaking.
     *
     * <p>
     *     Observer.onComplete() will never be called.
     *     <p> MRZ String is got in Observer.onNext()
     *
     */
    fun listen(): Observable<String>
}
