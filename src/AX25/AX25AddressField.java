package AX25;
/**
 * This class deals with the address field of the AX.25 frame
 * @author Anoop Santhosh
 * @version 1.0
 */
public class AX25AddressField {

	// Call sign string 6 letters
	public String CallSign;
	
	// Secondary Station Identifier
	public byte Ssid;
	
	
	// SSID Mask
    private static final byte SSIDMask = 0;
    
	/**
	 * AX.25 Address Field constructor  without any arguments
	 */
	public AX25AddressField(){
		this.CallSign = "000000";
		this.Ssid = 0;
	}
	
	/**
	 * AX.25 Address Field constructor 
	 * @param callSign the call sign of the address field
	 * @param ssid Secondary Station Identifier
	 */
	public AX25AddressField(String callSign, byte ssid){
		this.CallSign = callSign;
		this.Ssid = ssid;
	}
	
	/**
	 * AX.25 Adrress Field constructor to construct address field from a byte array
	 * @param framePart byte array of the frame
	 */
	public AX25AddressField(byte[] framePart){
		this(framePart,0);
	}
	
	/**
	 * AX.25 Adrress Field constructor to construct from a byte array and an offset
	 * @param framePart byte array of the frame
	 * @param offset the point to start with 
	 */
	public AX25AddressField(byte[] framePart,int offset){
		
		// Call Sign
		char[] tmp = new char[6];
		
		tmp[0] = (char)(framePart[offset] >> 1);
		tmp[1] = (char)(framePart[offset + 1] >> 1);
		tmp[2] = (char)(framePart[offset + 2] >> 1);
		tmp[3] = (char)(framePart[offset + 3] >> 1);
		tmp[4] = (char)(framePart[offset + 4] >> 1);
		tmp[5] = (char)(framePart[offset + 5] >> 1);
		this.CallSign = new String(tmp);
		
		//SSID
		this.Ssid = (byte)((framePart[offset + 6]>>1) & 0xF);
	}
	
	/**
	 * Create a byte array for the address field object
	 * @param sourceAddress true indicates its user needs a source address
	 * @return byte[] the byte array representation of the address field 
	 */
	public byte[] ToByteArray(boolean sourceAddress){
        // Call Sign
        byte[] tmp = new byte[7];
        tmp[0] = (byte)(this.CallSign.charAt(0) << 1);
        tmp[1] = (byte)(this.CallSign.charAt(1) << 1);
        tmp[2] = (byte)(this.CallSign.charAt(2) << 1);
        tmp[3] = (byte)(this.CallSign.charAt(3) << 1);
        tmp[4] = (byte)(this.CallSign.charAt(4) << 1);
        tmp[5] = (byte)(this.CallSign.charAt(5) << 1);

        // SSID
        tmp[6] = (byte)((this.Ssid << 1) | this.SSIDMask);
        if (sourceAddress){
            tmp[6] |= 0x01;
        }

        return tmp;
   }

	
	
}
