package BitOperations;
/**
 * This class deals with unsigned bianry operations
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class BitOperations {

	/**
	 * Function for converting a byte to unsigned binary string
	 * @param number The byte to be converted
	 * @return String string representation of the number
	 */
	public static String byteToBinaryString8(byte number){
		
		if(number >= 0 && number <=127 ){
			String temp = Integer.toBinaryString(number);
			int no_zero = 8 -temp.length();
			
			for(int i =0 ;i < no_zero;i++){
				temp = "0"+temp;
		
			}
			return temp;
		}else{
			return Integer.toBinaryString(number).substring(Integer.SIZE - Byte.SIZE);
		}
	}
	
	/**
	 * Function for converting an integer (less than 256) to a unsigned byte
	 * @param number the number to be converted
	 * @return byte
	 */
	public static byte IntegerToUnsignedbyte8(int number){
		String Trep = byteToBinaryString8((byte)number);
		int Tint = Integer.valueOf(Trep, 2);
		return (byte)Tint;

	}
	
	/**
	 * Function for converting an unsigned byte to an integer
	 * @param number the byte to be converted to integere
	 * @return integer the byte in integer
	 */
	public static int UnsignedBytetoInteger8(byte number){
		String Trep = byteToBinaryString8(number);
		return Integer.valueOf(Trep, 2);
	}
	
	/**
	 * Function for converting a 16 bit number to an unsignedinterger
	 * @param byte [] byte value of the number
	 * @ return integer value of the number
	 */
	public static int UnsignedBytetoInteger16(byte [] number){
		String s1 = byteToBinaryString8(number[0]);
		String s2 = byteToBinaryString8(number[1]);

		String fin = s1+s2;
		return Integer.valueOf(fin, 2);
	}
}
