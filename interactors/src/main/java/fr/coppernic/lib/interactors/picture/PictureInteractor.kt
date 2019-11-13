package fr.coppernic.lib.interactors.picture

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import fr.coppernic.lib.interactors.common.errors.InteractorException
import fr.coppernic.lib.interactors.common.ui.ActivityResultListener
import fr.coppernic.sdk.utils.debug.ObjPrinter
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import timber.log.Timber
import java.io.File
import javax.inject.Inject

const val AUTH_SUFFIX = ".fr.coppernic.lib.interactors.provider"

class PictureInteractor @Inject constructor() : ActivityResultListener {

    @Inject
    lateinit var context: Context

    private var subject: SingleSubject<Uri> = SingleSubject.create()

    private var request = Request(File(""), Uri.EMPTY, 0)

    /**
     * Launch default camera app to take a picture.
     *
     * @param file File where picture will be saved
     *
     * @return [Uri] Uri used by this lib via a [Single]
     */
    @Synchronized
    fun trig(file: File, activity: Activity): Single<Uri> {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cn = intent.resolveActivity(activity.packageManager)
        return when {
            cn == null -> Single.error(ActivityNotFoundException())
            request.isOnGoing() -> Single.error(InteractorException("Pending request in progress: $request"))
            else -> {
                subject = SingleSubject.create()
                try {
                    setup(activity, file)
                    activity.startActivityForResult(makeIntent(), request.id)
                    subject
                } catch (e: Exception) {
                    Single.error<Uri>(e)
                }
            }
        }
    }

    /**
     * To be called by the activity passed to [trig] for this interactor to be notified when picture has been taken.
     *
     * Put the same param than [Activity.onActivityResult]
     */
    @Synchronized
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.v("code $requestCode, result $resultCode, data ${ObjPrinter.bundleToString(data?.extras)}")

        when {
            requestCode != request.id -> Timber.v("Got request $requestCode instead of ${request.id}, waiting for our id...")
            resultCode != Activity.RESULT_OK -> subject.onError(InteractorException("Taking picture failed, result $resultCode"))
            else -> {
                subject.onSuccess(request.uri)
                request.clear()
            }
        }
    }

    private fun makeIntent() = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, request.uri)
        val resInfoList = context.packageManager.queryIntentActivities(this, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName,
                    request.uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun setup(context: Context, file: File) {
        val uriForCam = FileProvider.getUriForFile(context, getAuth(context), file)

        request = Request(file, uriForCam, file.hashCode())
    }

    private fun getAuth(context: Context): String {
        return "${context.packageName}$AUTH_SUFFIX"
    }
}

private data class Request(var file: File,
                           var uri: Uri,
                           var id: Int) {
    fun isOnGoing() = id != 0
    fun clear() {
        id = 0
    }
}
