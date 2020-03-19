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
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean

class IclassInteractor(private val context: Context,
                       private val port: String = Defines.SerialDefines.HID_ICLASS_PROX_READER_PORT,
                       private val baudRate: Int = 9600) {

    private var emitter: ObservableEmitter<ByteArray>? = null
    private var serial: SerialCom? = null
    private var serialThreadListener: SerialThreadListener? = null

    private val instanceListener = object : InstanceListener<SerialCom> {
        override fun onDisposed(p0: SerialCom) {
            LOG.debug("Serial com disposed")
        }

        override fun onCreated(s: SerialCom) {
            serial = s
            s.open(port, baudRate)
            s.flush()
            serialThreadListener = SerialThreadListener(s) {
                LOG.debug("frame received ${CpcBytes.byteArrayToString(it)}")
                if (it.size >= 4) {
                    if(verbose) {
                        LOG.debug("Frame received ${CpcBytes.byteArrayToString(it)}")
                    }
                    if (checkFrame(it)) {
                        onNext(it)
                    } else {
                        onError(IclassInteractorException("Length or CRC16 error"))
                    }
                }
            }
            serialThreadListener?.start()
        }
    }

    fun listen(): Observable<ByteArray> {
        return Observable.create { e ->
            setEmitter(e)
            SerialFactory.getInstance(context, SerialCom.Type.DIRECT, instanceListener)
        }
    }

    private fun onNext(data: ByteArray) {
        emitter?.apply {
            if (!isDisposed) {
                val pacs = getPACSData(data)
                LOG.debug("PACS data : $pacs")
                pacs?.let { onNext(it) }
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
            }
        }

        serialThreadListener?.stop()
        serial?.close()

        emitter = e.apply {
            setDisposable(object : Disposable {
                private val disposed = AtomicBoolean(false)

                override fun dispose() {
                    if (InteractorsDefines.verbose) {
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

    fun getPACSData(data: ByteArray) : ByteArray? {
        //remove header and CRC16
        val frame = data.copyOfRange(8,data.size - 2)
        if (frame[0] == 0xBD.toByte()) {
            val ced = frame.drop(6) // Remove PAYLOAD_RESPONSE TAG
            val padding = ced[0].toInt()
            val cedWithoutPadding = ced.drop(1).toByteArray()
            if(cedWithoutPadding.size <= 4) { //Int
                // shift right 6 to get pacs data
                val c = CpcBytes.byteArrayToInt(cedWithoutPadding, true)
                val iValue = ByteBuffer.wrap(cedWithoutPadding).int shr padding
                return CpcBytes.intToByteArray(iValue, true)
            } else if(ced.size in 5..8) { // Long
                // shift right 6 to get pacs data
                val lVal = CpcBytes.byteArrayToLong(cedWithoutPadding, true) shr padding
                return longToByteArray(lVal, true)
            }
        }
        return null
    }

    private fun longToByteArray(value: Long, bigEndian: Boolean): ByteArray? {
        val b = ByteBuffer.allocate(8)
        b.order(if (bigEndian) ByteOrder.BIG_ENDIAN else ByteOrder.LITTLE_ENDIAN)
        b.putLong(value)
        return b.array()
    }
}
