package fr.coppernic.lib.interactors.ocr.doubango

import android.app.Activity
import android.content.Intent
import fr.coppernic.lib.ocr.doubango.model.Mrz
import fr.coppernic.lib.utils.rx.error
import fr.coppernic.lib.utils.rx.success
import io.reactivex.Single
import io.reactivex.SingleEmitter

class OcrCameraInteractor {

    private var emitter: SingleEmitter<Mrz>? = null

    @Synchronized
    fun scan(activity: Activity): Single<Mrz> {
        return Single.create {
            emitter = it
            OcrCameraActivity.start(activity)
        }
    }

    @Synchronized
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OcrCameraActivity.REQUEST_CODE) {
            try {
                emitter.success(getResult(resultCode, data))
            } catch (e: Exception) {
                emitter.error(e)
            }
        }//no else
    }

    private fun getResult(resultCode: Int, data: Intent?): Mrz {
        return if (resultCode == Activity.RESULT_OK) {
            data?.getSerializableExtra(OcrCameraActivity.KEY_MRZ) as Mrz? ?: throw OcrInteractorException()
        } else {
            throw OcrInteractorException()
        }
    }

}
