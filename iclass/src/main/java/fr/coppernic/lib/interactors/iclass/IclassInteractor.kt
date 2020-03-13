package fr.coppernic.lib.interactors.iclass

import android.content.Context
import android.os.SystemClock
import fr.coppernic.lib.interactors.iclass.InteractorsDefines.LOG
import fr.coppernic.sdk.core.Defines
import fr.coppernic.sdk.serial.SerialCom
import fr.coppernic.sdk.serial.SerialFactory
import fr.coppernic.sdk.utils.core.CpcBytes
import fr.coppernic.sdk.utils.io.InstanceListener
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

class IclassInteractor(private val context: Context,
                       private val port: String = Defines.SerialDefines.HID_ICLASS_PROX_READER_PORT,
                       private val baudRate: Int = 9600) {

    private var emitter: ObservableEmitter<ByteArray>? = null
    private var serial: SerialCom? = null

    private val instanceListener = object : InstanceListener<SerialCom> {
        override fun onDisposed(p0: SerialCom) {
        }

        override fun onCreated(s: SerialCom) {
            serial = s
            serial?.open(Defines.SerialDefines.HID_ICLASS_PROX_READER_PORT, baudRate)
            receiveCom()
        }
    }

    fun listen(): Observable<ByteArray> {
        return Observable.create { e ->
            setEmitter(e)
            SerialFactory.getInstance(context, SerialCom.Type.DIRECT, instanceListener)
        }
    }

    private fun receiveCom() {
        while (!emitter?.isDisposed!!) {
            // wait byte received
            var bytesReceived = 0
            serial?.flush()
            while (bytesReceived == 0 && !emitter?.isDisposed!!) {
                bytesReceived = serial?.queueStatus ?: return
                SystemClock.sleep(10)
            }

            // We wait 20ms (10ms previous loop + 10ms here)
            // to ensure that all the frame is available.
            SystemClock.sleep(10)

            bytesReceived = serial?.queueStatus ?: return
            if (bytesReceived >= 4) {
                val currentFrame = ByteArray(bytesReceived)
                if (serial!!.receive(500, bytesReceived, currentFrame) > 0) {
                    LOG.debug("Frame received ${CpcBytes.byteArrayToString(currentFrame)}")
                    if (checkFrame(currentFrame)) {
                        onNext(currentFrame)
                    } else {
                        onError(Exception("Length or CRC16 error"))
                    }
                }
            }
        }
    }

    private fun onNext(data: ByteArray) {
        emitter?.apply {
            if (!isDisposed) {
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

    private fun setEmitter(e: ObservableEmitter<ByteArray>) {
        LOG.debug(e.toString())

        // End previous observer and start new one
        emitter?.apply {
            if (!isDisposed) {
                onComplete()
                serial?.close()
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
        return (crc16[1] == iCrc16.toByte() && /*data.dropLast(1).last()*/ crc16[0] == (iCrc16 shr 8).toByte())
    }
}
