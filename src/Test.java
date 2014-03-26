import BitOperations.BitOperations;


public class Test {

	public static void main (String [] args){
	
		byte b1 = (byte)255;
	
		
		System.out.println(BitOperations.IntegerToUnsignedbyte8(b1));
		byte b2 = BitOperations.IntegerToUnsignedbyte8(b1);
		System.out.println(BitOperations.UnsignedBytetoInteger8(b2));
	}
}
