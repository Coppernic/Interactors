package fr.coppernic.lib.interactors.barcode.zxing

import android.app.Activity
import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.*

class BarcodeZxingInteractor {

    private var emitter: SingleEmitter<String>? = null

    @Synchronized
    fun scan(activity: Activity): Single<String> {
        return Single.create {
            emitter = it
            IntentIntegrator(activity).apply {
                initiateScan()
            }
        }
    }

    @Synchronized
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            //if qrcode has nothing in it
            if (result.contents == null) {
                emitter?.onError(Throwable("Result not found"))
            } else {
                emitter?.onSuccess(result.contents)
            }
        }
    }
}