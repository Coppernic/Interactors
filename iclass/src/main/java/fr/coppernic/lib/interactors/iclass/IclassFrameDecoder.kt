package fr.coppernic.lib.interactors.iclass

import fr.coppernic.sdk.utils.core.CpcBytes
import java.nio.ByteBuffer
import java.nio.ByteOrder

class IclassFrameDecoder(val frame: ByteArray) {
    var type: Type? = null
    var cardNumber = 0L
    var facilityCode = -1
    var companyCode = -1
    val pacs = getPACSData(frame)



    private fun getPACSData(data: ByteArray): ByteArray? {
        //remove header and CRC16
        val frame = data.copyOfRange(8, data.size - 2)
        if (frame[0] == 0xBD.toByte()) {
            if (frame[4] == 0x81.toByte()) {
                type = Type.HF
            } else if (frame[4] == 0x80.toByte()) {
                type = Type.LF
            }
            val pacsLength = frame[5].toInt() - 1
            val ced = frame.drop(6) // Remove PAYLOAD_RESPONSE TAG
            val padding = ced[0].toInt()
            val cedWithoutPadding = ced.drop(1).toByteArray()
            if (cedWithoutPadding.size <= 4) { //Int Wiegand 26
                // shift right to get pacs data
                val iValue = ByteBuffer.wrap(cedWithoutPadding).int shr padding
                cardNumber = (iValue shr 1 and 0x0000FFFF).toLong()
                facilityCode = iValue shr 1 + 16 and 0x000000FF
                return CpcBytes.intToByteArray(iValue, true)
            } else if (cedWithoutPadding.size in 5..8) { // Long
                // shift right  to get pacs data
                val lVal = CpcBytes.byteArrayToLong(cedWithoutPadding, true) shr padding
                if(cedWithoutPadding.size == 5) {
                    // Wiegand 37 bit
                    cardNumber = lVal shr 1 and 0x6FFFFFFFF //card number is 35bit
                    //corporate 1000 35bit
                    //cardNumber = lVal shr 1 and 0x0FFFFF //card number is 20bit
                    // companyCode = (lVal shr 1 + 20 and 0x0FFF).toInt()
                } else if(cedWithoutPadding.size == 6){ //Corporate 1000 48bit
                    cardNumber = lVal shr 1 and 0x7FFFFF //card number is 23bit
                    companyCode = (lVal shr 1 + 23 and 0x3FFFFF).toInt()
                }
                return longToByteArray(lVal, true)?.drop(8 - pacsLength)?.toByteArray()
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
