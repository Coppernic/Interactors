package fr.coppernic.lib.interactors.ocr

import android.content.Context
import android.os.Build
import fr.coppernic.sdk.utils.helpers.OsHelper

object OcrInteractorBuilder {
    fun build(context: Context): OcrInteractor {
        return when {
            OsHelper.isIdPlatform() -> {
                ElyctisInteractor(context)
            }
            OsHelper.isCone() -> {
                AccessIsInteractor(context)
            }
            else -> {
                throw OcrInteractorException("Unknown device ${Build.DEVICE} and model ${Build.MODEL}, cannot find suitable OcrInteractor " +
                        "implementation")
            }
        }
    }
}
