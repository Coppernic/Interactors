package fr.coppernic.lib.interactors.picture

import android.os.Environment
import java.io.File
import javax.inject.Inject

/**
 * Provider interface that is used by interactor to get path of file to save
 */
interface PictureInteractorStorageProvider {
    /**
     * Return a file that may not exist yet.
     *
     * File must be on a writable storage. This method is called by Picture interactor to get path of picture to save.
     *
     * @param id Id put by caller in [PictureInteractor.trig] method
     *
     * @return File that can be created
     */
    fun getPictureFileForId(id: String): File
}

const val PICTURE_EXT = ".jpg"

/**
 * Simple implementation of [PictureInteractorStorageProvider] that saves picture in Picture folder of external storage
 */
class PictureInteractorStorageProviderImpl @Inject constructor() : PictureInteractorStorageProvider {
    override fun getPictureFileForId(id: String): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).resolve(id).apply {
            mkdirs()
        }.resolve("$id$PICTURE_EXT")
    }
}
