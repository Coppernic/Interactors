package fr.coppernic.lib.interactors.mrtd

import android.app.Activity
import fr.coppernic.lib.interactors.mrtd.InteractorsDefines.LOG
import fr.coppernic.sdk.passport.Document
import fr.coppernic.sdk.passport.DocumentInterface
import fr.coppernic.sdk.passport.lds.IcaoFile
import fr.coppernic.sdk.utils.core.CpcResult
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe
import io.reactivex.disposables.Disposable
import java.security.cert.X509Certificate
import java.util.concurrent.atomic.AtomicBoolean

class MrtdInteractor() {
    private var status: Status = Status.IDLE
    private var emitter: SingleEmitter<DataGroup>? = null
    private var mrz: String = ""
    private val documentInterface: DocumentInterface = object : DocumentInterface {
        override fun onChipAuthenticationStarted() {
            if (InteractorsDefines.verbose) {
                LOG.trace("onChipAuthenticationStarted")
            }
        }

        override fun onPassiveAuthenticationEnded(p0: CpcResult.RESULT) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onPassiveAuthenticationEnded {}", p0)
            }
        }

        override fun onPassiveAuthenticationOperationFinished(p0: DocumentInterface.PassiveAuthenticationOperations, p1: CpcResult.RESULT) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onPassiveAuthenticationOperationFinished {} {}", p0, p1)
            }
        }

        override fun onSodInformationAvailable(p0: X509Certificate) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onSodInformationAvailable {}", p0)
            }
        }

        override fun onActiveAuthenticationProgress(p0: Int, p1: Int, p2: String) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onActiveAuthenticationProgress {} {} {}", p0, p1, p2)
            }
        }

        override fun onPassiveAuthenticationStarted() {
            if (InteractorsDefines.verbose) {
                LOG.trace("onPassiveAuthenticationStarted")
            }
        }

        override fun onActiveAuthenticationStarted() {
            if (InteractorsDefines.verbose) {
                LOG.trace("onActiveAuthenticationStarted")
            }
        }

        override fun onBacStarted() {
            if (InteractorsDefines.verbose) {
                LOG.trace("onBacStarted")
            }
        }

        override fun onFileRead(p0: IcaoFile.Files, p1: CpcResult.RESULT) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onFileRead {}, {}", p0, p1)
            }
            if (p1 == CpcResult.RESULT.OK) {
                when (p0) {
                    IcaoFile.Files.EFCOM -> {

                    }
                    IcaoFile.Files.DG1 -> {
                        mrz = document.dataGroup1.mrz.fullMrz
                        status = Status.DG2
                        document.read(2)
                    }
                    IcaoFile.Files.DG2 -> {
                        onSuccess(DataGroup(mrz, document.dataGroup2.bitmap))
                    }
                    IcaoFile.Files.DG3 -> TODO()
                    IcaoFile.Files.DG4 -> TODO()
                    IcaoFile.Files.DG5 -> TODO()
                    IcaoFile.Files.DG6 -> TODO()
                    IcaoFile.Files.DG7 -> TODO()
                    IcaoFile.Files.DG8 -> TODO()
                    IcaoFile.Files.DG9 -> TODO()
                    IcaoFile.Files.DG10 -> TODO()
                    IcaoFile.Files.DG11 -> TODO()
                    IcaoFile.Files.DG12 -> TODO()
                    IcaoFile.Files.DG13 -> TODO()
                    IcaoFile.Files.DG14 -> TODO()
                    IcaoFile.Files.DG15 -> TODO()
                    IcaoFile.Files.DG16 -> TODO()
                    IcaoFile.Files.SOD -> TODO()
                    IcaoFile.Files.CardAccess -> TODO()
                    IcaoFile.Files.CardSecurity -> TODO()
                    IcaoFile.Files.ChipSecurity -> TODO()
                }
            } else {
                onError(MrtdInteractorException("onFileRead error: $p1"))
            }
        }

        override fun onTerminalAuthenticationProgress(p0: Int, p1: Int, p2: String) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onTerminalAuthenticationProgress {}, {}, {}", p0, p1, p2)
            }
        }

        override fun onTerminalAuthenticationEnded(p0: CpcResult.RESULT, p1: String) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onTerminalAuthenticationEnded {}, {}", p0, p1)
            }
        }

        override fun onTerminalAuthenticationStarted() {
            if (InteractorsDefines.verbose) {
                LOG.trace("onTerminalAuthenticationStarted")
            }
        }

        override fun onChipAuthenticationProgress(p0: Int, p1: Int, p2: String) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onChipAuthenticationProgress {}, {}, {}", p0, p1, p2)
            }
        }

        override fun onStartReadingFile(p0: IcaoFile.Files) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onStartReadingFile {}", p0)
            }
        }

        override fun onDocumentConnected(p0: CpcResult.RESULT, p1: Boolean) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onDocumentConnected {}, {}", p0, p1)
            }
            if (p0 == CpcResult.RESULT.OK) {
                document.performBac(key)
            } else {
                onError(MrtdInteractorException("onDocumentConnected error: $p0"))
            }
        }

        override fun onWaitingForDocument() {
            if (InteractorsDefines.verbose) {
                LOG.trace("onWaitingForDocument")
            }
        }

        override fun onBacEnded(p0: CpcResult.RESULT) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onBacEnded {}", p0)
            }
            if (p0 == CpcResult.RESULT.OK) {
                document.read(1)
            } else {
                onError(MrtdInteractorException("onBacEnded error: $p0"))
            }
        }

        override fun onProgress(p0: Int, p1: Int) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onProgress {}, {}", p0, p1)
            }
        }

        override fun onChipAuthenticationEnded(p0: CpcResult.RESULT, p1: String) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onChipAuthenticationEnded {}, {}", p0, p1)
            }
        }

        override fun onActiveAuthenticationEnded(p0: CpcResult.RESULT, p1: String) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onActiveAuthenticationEnded {}, {}", p0, p1)
            }
        }

        override fun onReadFinished(p0: CpcResult.RESULT) {
            if (InteractorsDefines.verbose) {
                LOG.trace("onReadFinished {}", p0)
            }
        }
    }

    private lateinit var activity: Activity
    private lateinit var key: String
    private lateinit var options: Options
    private lateinit var document: Document

    private val singleOnSubscribe = SingleOnSubscribe<DataGroup> { e ->
        setEmitter(e)
        document = Document(activity, documentInterface, options.readerType.type)
        document.connect(options.timeout.toInt())
    }

    @Synchronized
    fun listen(activity: Activity,
               key: String,
               options: Options = Options()): Single<DataGroup> {
        return if (status != Status.IDLE) {
            Single.error(MrtdInteractorException("Busy"))
        } else {
            status = Status.DG1
            this.activity = activity
            this.key = key
            this.options = options
            Single.create(singleOnSubscribe).doFinally {
                // Reset status here
                status = Status.IDLE
            }
        }
    }

    private fun onSuccess(dataGroup: DataGroup) {
        emitter?.apply {
            if (!isDisposed) {
                onSuccess(dataGroup)
            }
        }
    }

    private fun onError(e: Exception) {
        emitter?.apply {
            if (!isDisposed) {
                onError(e)
            }
        }
    }

    private fun setEmitter(e: SingleEmitter<DataGroup>) {
        LOG.debug(e.toString())

        // End previous observer and start new one
        emitter?.apply {
            if (!isDisposed) {
                // FIXME disconnect ?
            }
        }
        emitter = e.apply {
            setDisposable(object : Disposable {
                private val disposed = AtomicBoolean(false)

                override fun dispose() {
                    if (InteractorsDefines.verbose) {
                        LOG.trace("dispose")
                    }
                    disposed.set(true)
                    // FIXME disconnect ?
                }

                override fun isDisposed(): Boolean {
                    return disposed.get()
                }
            })
        }
    }

    data class Options(val readerType: ReaderType = ReaderType.CONE_PCSC_RFID, val timeout: Long = 30000)

    enum class ReaderType(val type: Document.Readers) {
        CONE_PCSC_RFID(Document.Readers.C_One_e_ID_Rfid),
        ANDROID_NFC(Document.Readers.Android_Nfc);
    }

    private enum class Status {
        IDLE,
        DG1,
        DG2,
    }

}
