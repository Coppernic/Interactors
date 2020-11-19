package fr.coppernic.lib.interactors.iclass

import fr.coppernic.sdk.utils.core.CpcBytes
import java.nio.ByteBuffer
import kotlin.experimental.and

private const val PADDING_COPORATE_1000_35_BIT = 5
private const val PADDING_WIEGAND_37 = 3
private const val HEADER_LENGTH = 8
private const val CARD_NUMBER_WIEGAND_26BIT_LENGTH = 16
private const val CARD_NUMBER_37_BIT_WITH_FC_LENGTH = 19
private const val CARD_NUMBER_CORP_1000_48_BIT_LENGTH = 23
private const val CARD_NUMBER_CORP_1000_35_BIT_LENGTH = 20

private const val MASK_8_BIT = 0xFF
private const val MASK_12_BIT = 0xFFFL
private const val MASK_16_BIT = 0xFFFF
private const val MASK_19_BIT = 0x7FFFFL
private const val MASK_20_BIT = 0xFFFFFL
private const val MASK_22_BIT = 0x3FFFFFL
private const val MASK_23_BIT = 0x7FFFFFL
private const val MASK_26_BIT = 0x3FFFFFF
private const val MASK_35_BIT = 0x6FFFFFFFFL

private const val FRAME_LENGTH_INDEX = 1
private const val TYPE_INDEX = HEADER_LENGTH + 4
private const val PACS_LENGTH_INDEX = HEADER_LENGTH + 5
private const val PADDING_INDEX = HEADER_LENGTH + 6
private const val RESPONSE_OK = 0xBD.toByte()
private const val CRC16_LENGTH = 2
private const val LENGTH_VALUE_LENGTH = 2

class IClassFrame(val frame: ByteArray, var withFacilityCode: Boolean = false) {
    var type = Type.Unknown
    var cardNumber = 0L
    var facilityCode = -1
    var companyCode = -1
    val pacs = getPACSData(frame)
    val pacsBinaryString = pacs.toBinaryString()

    private fun getPACSData(frame: ByteArray): ByteArray {
        if (frame.size > LENGTH_VALUE_LENGTH && frame[FRAME_LENGTH_INDEX].toInt() == frame.size - CRC16_LENGTH - LENGTH_VALUE_LENGTH
                && frame[HEADER_LENGTH] == RESPONSE_OK) {
            when (frame[TYPE_INDEX]) {
                Type.HF.value -> type = Type.HF
                Type.LF.value -> type = Type.LF
                else -> Type.Unknown
            }
            val pacsLength = frame[PACS_LENGTH_INDEX].toInt() - 1 //without padding byte
            val padding = frame[PADDING_INDEX].toInt()
            //Remove Header, CRC16 and padding
            val cedWithoutPadding = frame.copyOfRange(PADDING_INDEX + 1, frame.size - CRC16_LENGTH)
            if (cedWithoutPadding.size <= 4) { //Int Wiegand 26
                // shift right to get pacs data
                var iValue = ByteBuffer.wrap(cedWithoutPadding).int shr padding
                cardNumber = (iValue shr 1 and MASK_16_BIT).toLong()
                facilityCode = (iValue shr 1 + CARD_NUMBER_WIEGAND_26BIT_LENGTH and MASK_8_BIT)
                if(iValue < 0){
                    iValue = iValue and MASK_26_BIT
                }
                return CpcBytes.intToByteArray(iValue, true)
            } else if (cedWithoutPadding.size in 5..8) { // Long
                // shift right  to get pacs data
                val lVal = CpcBytes.byteArrayToLong(cedWithoutPadding, true) shr padding
                if (cedWithoutPadding.size == 5) {
                    if (padding == PADDING_WIEGAND_37) {// Wiegand 37 bit
                        if (withFacilityCode) {//with facility code
                            cardNumber = lVal shr 1 and MASK_19_BIT //card number is 19bit
                            facilityCode = (lVal shr 1 + CARD_NUMBER_37_BIT_WITH_FC_LENGTH and MASK_16_BIT.toLong()).toInt()
                        } else {
                            cardNumber = lVal shr 1 and MASK_35_BIT //card number is 35bit
                        }
                    } else if (padding == PADDING_COPORATE_1000_35_BIT) { //corporate 1000 35bit
                        cardNumber = lVal shr 1 and MASK_20_BIT //card number is 20bit
                        companyCode = (lVal shr 1 + CARD_NUMBER_CORP_1000_35_BIT_LENGTH and MASK_12_BIT).toInt()
                    }
                } else if (cedWithoutPadding.size == 6) { //Corporate 1000 48bit
                    cardNumber = lVal shr 1 and MASK_23_BIT //card number is 23bit
                    companyCode = (lVal shr 1 + CARD_NUMBER_CORP_1000_48_BIT_LENGTH and MASK_22_BIT).toInt()
                }
                return CpcBytes.longToByteArray(lVal, true)?.drop(8 - pacsLength)?.toByteArray()
                        ?: byteArrayOf()
            }
        }
        return byteArrayOf()
    }
}

enum class Type(val value: Byte) {
    HF(0x81.toByte()),
    LF(0x80.toByte()),
    Unknown(0x00)
}

fun ByteArray.toBinaryString(): String{
    var s = "";
    this.forEach {
        val temp = String.format("%8s", Integer.toBinaryString((it and 0xFF.toByte()).toInt())).replace(' ', '0')
        s += temp.substring(temp.length - 8)
    }
    return s;
}
