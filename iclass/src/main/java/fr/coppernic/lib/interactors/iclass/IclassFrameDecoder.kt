package fr.coppernic.lib.interactors.iclass

import fr.coppernic.sdk.utils.core.CpcBytes
import java.nio.ByteBuffer
import java.nio.ByteOrder

class IclassFrameDecoder(val frame: ByteArray) {

    val pacs = getPACSData(frame)
    var type: Type? = null

    private fun getPACSData(data: ByteArray): ByteArray? {
        //remove header and CRC16
        val frame = data.copyOfRange(8, data.size - 2)
        if (frame[0] == 0xBD.toByte()) {
            if (frame[4] == 0x81.toByte()) {
                type = Type.HF
            } else if (frame[4] == 0x80.toByte()) {
                type = Type.LF
            }
            val ced = frame.drop(6) // Remove PAYLOAD_RESPONSE TAG
            val padding = ced[0].toInt()
            val cedWithoutPadding = ced.drop(1).toByteArray()
            if (cedWithoutPadding.size <= 4) { //Int
                // shift right 6 to get pacs data
                val c = CpcBytes.byteArrayToInt(cedWithoutPadding, true)
                val iValue = ByteBuffer.wrap(cedWithoutPadding).int shr padding
                return CpcBytes.intToByteArray(iValue, true)
            } else if (ced.size in 5..8) { // Long
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

enum class Type {
    HF,
    LF
}
