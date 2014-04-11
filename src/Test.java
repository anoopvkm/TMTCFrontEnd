import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import Trace.Trace;
import BitOperations.BitOperations;


public class Test {

	public static void main (String [] args) throws IOException{
	
		byte b1 = (byte)255;
	
		
		int width = 400;
		System.out.println(Integer.toBinaryString(width));
		byte [] blah = new byte[2];
		blah[1] = (byte) (width & 0xFF);;
		blah[0] = (byte) ((width >> 8) & 0xFF);
		System.out.println(BitOperations.byteToBinaryString8(blah[0]) + " " +BitOperations.byteToBinaryString8(blah[1]) );
	}
}
