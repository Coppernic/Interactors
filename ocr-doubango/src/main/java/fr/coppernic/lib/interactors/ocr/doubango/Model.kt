package fr.coppernic.lib.interactors.ocr.doubango

import fr.coppernic.lib.ocr.doubango.model.Mrz
import java.io.Serializable

data class OcrInteractorConfig(val finishOnOcr: Boolean = true) : Serializable

interface OcrCameraListener {
    fun onMrz(mrz: Mrz)
}
