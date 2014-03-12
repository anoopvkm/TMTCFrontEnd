/**
 * This class deals with frame identification part of AX.25 telemetry secondary packet header
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class AX25FrameIdentification {

	// Version Number
	private static byte VersionNumber = 0x00;
	
	// Spare bits
	private static byte SpareBits = 0X00;
	
	// Virtual channel ID
	public byte VirtualChannelID ;
	
	/**
	 * Constructor for AX.25 Frame Identification class
	 */
	public AX25FrameIdentification(){
		this.VirtualChannelID = 0;
	}
	
	/**
	 * Conctructor for AX.25 Frame identification class from vcid
	 * @param vcid Virtual channel ID
	 */
	public AX25FrameIdentification(byte vcid){
		this.VirtualChannelID = vcid;
	}
	
	/**
	 * Constructor for AX.25 Frame Identification class from a byte array
	 * @param framePart AX.25 Frame part
	 */
	public AX25FrameIdentification(byte[] framePart){
		this(framePart,0);
	}
	
	/**
	 * Constructor for AX.25 frame identification class from a byte array and a given offset
	 * @param framePart AX.25 Frame part
	 * @param offset offset into the byte array
	 */
	public AX25FrameIdentification(byte[] framePart,int offset){
		
		 // Version number
		 if (((byte)(framePart[offset] >> 6)) != AX25FrameIdentification.VersionNumber){
              // TODO add exception 
         }
		 
		 // Spare bits
		 if (((byte)(framePart[offset] & 0x07)) != AX25FrameIdentification.SpareBits){
              // TODO add exception
         }
		 
		 if (((byte)(framePart[offset] & 0x07)) != AX25FrameIdentification.SpareBits){
             // TODO add exception
         }
	}
	
	/**
	 * Function returns a byte array for the frame identification part
	 * @return byte array
	 */
	public byte[] ToByteArray(){
		
          byte[] tmp = new byte[1];

          // Version Number
          tmp[0] = (byte)(AX25FrameIdentification.VersionNumber << 6);

          // Virtual Channel ID
          tmp[0] |= (byte)(this.VirtualChannelID << 3);

          // Version Number
          tmp[0] |= AX25FrameIdentification.SpareBits;

          return tmp;
     }
}
