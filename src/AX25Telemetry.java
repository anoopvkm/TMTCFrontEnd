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
	
	
	
}
