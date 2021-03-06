package fr.coppernic.lib.interactors.iclass

import android.content.Context
import fr.coppernic.lib.interactors.iclass.InteractorsDefines.LOG
import fr.coppernic.lib.interactors.iclass.InteractorsDefines.verbose
import fr.coppernic.sdk.core.Defines
import fr.coppernic.sdk.serial.SerialCom
import fr.coppernic.sdk.serial.SerialFactory
import fr.coppernic.sdk.serial.utils.SerialThreadListener
import fr.coppernic.sdk.utils.core.CpcBytes
import fr.coppernic.sdk.utils.io.InstanceListener
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

class IClassInteractor(private val context: Context,
                       private val port: String = Defines.SerialDefines.HID_ICLASS_PROX_READER_PORT,
                       private val baudRate: Int = 9600) {

    private var emitter: ObservableEmitter<IClassFrame>? = null
    private var serial: SerialCom? = null
    private var serialThreadListener: SerialThreadListener? = null

    private val instanceListener = object : InstanceListener<SerialCom> {
        override fun onDisposed(p0: SerialCom) {
            LOG.debug("Serial com disposed")
        }

        override fun onCreated(s: SerialCom) {
            serial = s
            if (s.open(port, baudRate) != SerialCom.ERROR_OK) {
                onError(IclassInteractorException("Error opening serial port"))
                return
            }
            s.flush()
            serialThreadListener = SerialThreadListener(s) {
                if (verbose) {
                    LOG.debug("frame received ${CpcBytes.byteArrayToString(it)}")
                }
                if (it.size >= 4 && checkFrame(it)) {
                    onNext(IClassFrame(it))

                } else {
                    onError(IclassInteractorException("Length or CRC16 error"))
                }
            }
            serialThreadListener?.start()

        }
    }

    fun listen(): Observable<IClassFrame> {
        return Observable.create { e ->
            setEmitter(e)
            SerialFactory.getInstance(context, SerialCom.Type.DIRECT, instanceListener)
        }
    }

    private fun onNext(data: IClassFrame) {
        emitter?.apply {
            if (!isDisposed) {
                LOG.debug("Frame data : ${data.frame}")
                onNext(data)
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

    private fun setEmitter(e: ObservableEmitter<IClassFrame>) {
        LOG.debug(e.toString())

        // End previous observer and start new one
        emitter?.apply {
            if (!isDisposed) {
                onComplete()
            }
        }

        serialThreadListener?.stop()
        serial?.close()

        emitter = e.apply {
            setDisposable(object : Disposable {
                private val disposed = AtomicBoolean(false)

                override fun dispose() {
                    if (verbose) {
                        LOG.trace("dispose")
                    }
                    disposed.set(true)
                    serialThreadListener?.stop()
                    serial?.close()
                }

                override fun isDisposed(): Boolean {
                    return disposed.get()
                }
            })
        }
    }

    private fun checkFrame(data: ByteArray): Boolean {
        // frame length in two first byte + 2 CRC16 + 2 byte of length
        val frameLength = CpcBytes.byteArrayToShort(byteArrayOf(data[0], data[1]), true) + 4
        LOG.debug("frame length : $frameLength")
        return (data.size == frameLength && checkCrc16(data))
    }

    private fun checkCrc16(data: ByteArray): Boolean {
        val dataWithoutCrc16 = data.dropLast(2).toByteArray()
        val crc16 = data.copyOfRange(data.size - 2, data.size)
        val iCrc16 = CrcUtils.computeCRC(dataWithoutCrc16)
        return (crc16[1] == iCrc16.toByte() && crc16[0] == (iCrc16 shr 8).toByte())
    }
}
