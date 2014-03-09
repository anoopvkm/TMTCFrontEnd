
/**
 * This class deals with the a telecommand packet
 * It inherets from AX.25 frame .
 * Not a lot of things are drastically different.
 * @version 1.0
 */
public class AX25Telecommand extends AX25Frame {
	
	/**
	 * Constructor for AX.25 telecommand
	 */
	public AX25Telecommand(){
		super();
	}
	
	/**
	 * Constructor for AX.25 Telecommand class takes in a frame as an argument
	 * @param frame : AX.25 Frame
	 */
	public AX25Telecommand(byte[] frame){
		super(frame);
	}
	
	/**
	 * Constructor for AX.25 Telecommand 
	 * @param frame : AX.25 Frame
	 * @param offset : offset into the byte array
	 */
	public AX25Telecommand(byte[] frame, int offset){
         super (frame, offset);
    }

	/**
	 * Construtor for AX 25 Telecommand
	 * @param dstAddress : destination address field
	 * @param dstAddress : source address field
	 * @param informationField : Information Field
	 */
	public AX25Telecommand(AX25AddressField dstAddress, AX25AddressField srcAddress, byte[] informationField){
         super (dstAddress, srcAddress, informationField);
    }

}
