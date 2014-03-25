package AX25;
/**
 * This class deals with AX.25 telemetry packets
 * @author Anoop R Santhosh
 * @version 1.0
 */

public class AX25Telemetry extends AX25Frame{

	// Secondary header length in bytes
	public static int SecondaryHeaderLength = 4;
	
	// AX.25 Frame Identification
	public AX25FrameIdentification FrameIdentification;
	
	// Master frame count
	public byte MasterFrameCount;
	
	// Virtual channel frame count
	public byte VirtualChannelFrameCount;
	
	// First Header pointer
	public byte FirstHeaderPointer;
	
	// Information Field
    public byte[] Data = new byte [0];
    
	// AX.25 Frame status
	public AX25FrameStatus FrameStatus;
	
	// Time field
	public long time;
	
	/**
	 * Constructor for AX 25 telemetry class
	 */
	public AX25Telemetry () {
		super();
	}
	
	/**
	 * Constrcutor for AX 25 Telemetry class from a byte array
	 * @param frame AX.25 frame
	 */
	public AX25Telemetry(byte [] frame){
		super(frame);
	}
	
	/**
	 * Constructor for AX 25 Telemetry clas from a byte array and an offset
	 * @param frame AX.25 frame
	 * @param offset offset into the byte array
	 */
	public AX25Telemetry(byte [] frame,int offset){
		super(frame,offset);
	}
	
	/**
	 * Constructor for AX.25 Telemetry class from values provided 
	 * @param dstAddress Destination Address 
	 * @param srcAddress Source Address
	 * @param frameIdentification Frame Identification value 
	 * @param masterFrameCount Master Frame Count
	 * @param virtualChannelFrameCount Virtual Channel Frame count
	 * @param firstHeaderPointer : First Header Pointer 
	 * @param data : Data Field 
	 * @param frameStatus : Frame status 
	 * @param time : time field 
	 */
	public AX25Telemetry(AX25AddressField dstAddress, AX25AddressField srcAddress, AX25FrameIdentification frameIdentification, byte masterFrameCount, byte virtualChannelFrameCount, byte firstHeaderPointer, byte[] data, AX25FrameStatus frameStatus, long time){
	          
		super (dstAddress, srcAddress, new byte [0]);
	    this.FrameIdentification = frameIdentification;
	    this.MasterFrameCount = masterFrameCount;
	    this.VirtualChannelFrameCount = virtualChannelFrameCount;
	    this.FirstHeaderPointer = firstHeaderPointer;
	    this.Data = data;
	    this.FrameStatus = frameStatus;
	    this.time = time;
	}
}
