package CRC;
/*************************************************************************
 *  Compilation:  javac CRC16CCITT.java
 *  Execution:    java CRC16CCITT s
 *  Dependencies: 
 *  
 *  Reads in a sequence of bytes and prints out its 16 bit
 *  Cylcic Redundancy Check (CRC-CCIIT 0xFFFF).
 *
 *  1 + x + x^5 + x^12 + x^16 is irreducible polynomial.
 *
 *  % java CRC16-CCITT 123456789
 *  CRC16-CCITT = 29b1
 *
 *************************************************************************/
/**
 * 
 * @source : http://introcs.cs.princeton.edu/java/51data/CRC16CCITT.java.html
 *
 */
public class CRC16CCITT { 

	/**
	 * this function generates crc for data byte array
	 * @param data the byte array of which we want to calculate cc
	 * @return byte[] CRC value
	 */
    public static byte[] generateCRC(byte [] data) { 
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 

        // byte[] testBytes = "123456789".getBytes("ASCII");

        byte [] result = new byte[2]; // byte[0] - higher bits ; byte[1] - higher bits 
        byte[] bytes = data;

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
             }
        }

        crc &= 0xffff;
        result[1] = (byte) (crc & 0xFF);;
		result[0] = (byte) ((crc >> 8) & 0xFF);
        return result;
    }

}
