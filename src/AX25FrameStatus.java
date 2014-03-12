/**
 * This class deals with frame identification part of AX.25 telemtry secondary packet trailer frame status bit
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class AX25FrameStatus {
	
	// Spare bits
	private static byte SpareBits = 0X00;
	
	// Time Flag
	public byte TimeFlag;
	
	// TC counter
	public byte TCCounter;

	/**
	 * Constructor for class. 
	 */
	public AX25FrameStatus(){
		this.TimeFlag = 0xB; // Default Value
	    this.TCCounter = 0;
	}

	/**
	 * Constructor for constructing class object from timeflag and tccounter
	 * @param timeFlag
	 * @param tcCounter
	 */
	 public AX25FrameStatus(byte timeFlag, byte tcCounter){
          this.TimeFlag = timeFlag;
          this.TCCounter = tcCounter;
     }

	/**
	 * Constructor for class from a byte array
	 * @param framebits a byte array
	 */
	public AX25FrameStatus(byte [] framebits){
		this(framebits,0);
	}
	
	/**
	 * Constructor for class from a byte array and an offset into the byte array
	 * @param framebits a byte array
	 * @param offset: offset into the byte array
	 */
	public AX25FrameStatus(byte [] framebits, int offset){
		 // Time Flag
        this.TimeFlag = (byte)(framebits[offset] >> 4);

        // Spare bits
        if (((byte)((framebits[offset] >> 2) & 0x03)) != AX25FrameStatus.SpareBits)
        {
           // TODO exception
        }

        // TC Counter
        this.TCCounter = (byte)(framebits[offset] & 0x03);
	}	
	
	/**
	 * Function returns the length of the time field
	 * @return length of the time field
	 */
    public int getTimeLength(){
        return (int)(this.TimeFlag >= 8 ? ((this.TimeFlag & 0x7) + 1) : 0);
    }
	
    /**
     * Function to convert the class object to a byte array
     * @return byte array of the object
     */
    public byte[] ToByteArray(){
    	
         byte[] tmp = new byte[1];

         // Time Flag
         tmp[0] = (byte)(this.TimeFlag << 4);

         // Spare
         tmp[0] |= (byte)(AX25FrameStatus.SpareBits << 2);

         // TC Counter
         tmp[0] |= this.TCCounter;

         return tmp;
    }
	

}
