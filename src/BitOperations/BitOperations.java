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
	 * @return String 
	 */
	public static String byteToBinaryString8(byte number){
		if(number >= 0 && number <=127 ){
			return "0"+Integer.toBinaryString(number);
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
	 * @param number
	 * @return integer
	 */
	public static int UnsignedBytetoInteger8(byte number){
		String Trep = byteToBinaryString8(number);
		return Integer.valueOf(Trep, 2);
	}
}
