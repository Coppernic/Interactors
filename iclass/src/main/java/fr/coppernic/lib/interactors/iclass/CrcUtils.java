package fr.coppernic.lib.interactors.iclass;

/**
 * Computes CRC16-KERMIT
 */
public class CrcUtils {

    /**
     * Computes CRC16-KERMIT over the data bytes in parameter
     * @param val data bytes
     * @return CRC16 result
     */
    static int computeCRC(byte[] val) {
        int crc;
        int q;
        byte c;
        crc = 0x0000;
        for (byte aVal : val) {
            c = aVal;
            q = (crc ^ c) & 0x0f;
            crc = (crc >> 4) ^ (q * 0x1081);
            q = (crc ^ (c >> 4)) & 0x0f;
            crc = (crc >> 4) ^ (q * 0x1081);
        }
        int crcEnd = crc;
        //Swap bytes
        int byte1 = (crcEnd & 0xff);
        int byte2 = ((crcEnd >> 8) & 0xff);
        return ((byte1 << 8) | (byte2));
    }
}
