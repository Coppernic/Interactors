package fr.coppernic.lib.interactors.barcode.zxing

import android.app.Activity
import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator
import fr.coppernic.lib.interactors.barcode.zxing.exceptions.BarcodeZxingException
import fr.coppernic.lib.utils.rx.error
import fr.coppernic.lib.utils.rx.success
import io.reactivex.*

private const val NOT_FOUND = "Result not found"

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
                emitter?.error(BarcodeZxingException(NOT_FOUND))
            } else {
                emitter?.success(result.contents)
            }
        }
    }
}
