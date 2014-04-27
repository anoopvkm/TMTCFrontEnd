﻿package AX25;

import BitOperations.BitOperations;

/**
 * This class deals with the a telecommand packet
 * It inherets from AX.25 frame .
 * Not a lot of things are drastically different.
 * @author Anoop R Santhosh 
 * @version 1.0
 */
public class AX25Telecommand extends AX25Frame {
	
	
	// Telecommand packet counter
	public byte TCCounter = 0;
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
		this.TCCounter = super.GetInformationField()[0];
	}
	
	/**
	 * Constructor for AX.25 Telecommand 
	 * @param frame : AX.25 Frame
	 * @param offset : offset into the byte array
	 */
	public AX25Telecommand(byte[] frame, int offset){
         super (frame, offset);
         this.TCCounter = super.GetInformationField()[0];
    }

	/**
	 * Construtor for AX 25 Telecommand
	 * @param dstAddress : destination address field
	 * @param dstAddress : source address field
	 * @param informationField : Information Field
	 */
	public AX25Telecommand(AX25AddressField dstAddress, AX25AddressField srcAddress, byte[] informationField,int counter){
		
         super (dstAddress, srcAddress, informationField);
         this.TCCounter = BitOperations.IntegerToUnsignedbyte8(counter);
    }

	/**
	 * Constructor for AX 25 telecommand
	 * @param dstAddress
	 * @param srcAddress
	 * @param informationField
	 */
	public AX25Telecommand(AX25AddressField dstAddress,AX25AddressField srcAddress,byte[] informationField){
		super(dstAddress,srcAddress,informationField);
	}
	
	/**
	 * Function to set the counter field 
	 * @param counter , integer 
	 */
	public void SetCounter(int counter){
		this.TCCounter = BitOperations.IntegerToUnsignedbyte8(counter);
	}
	
	/**
	 * Function to get the counter field as integer
	 */
	public int GetCounter(){
		return BitOperations.UnsignedBytetoInteger8(this.TCCounter);
	}
	/**
	 * Function to get frame length
	 */
	public int getFrameLength(){
		return (int)(AX25Frame.HeaderLength + this.GetInformationField().length + 1);
	}
	
	/**
	 * Function to convert an AXtelecommand to byte array
	 */
	
	public byte[] ToByteArray() {
		byte[] frame = new byte[this.getFrameLength()];
		
		// destination address field
		System.arraycopy(this.DstAddress.ToByteArray(false), 0, frame, 0, 7);
		
		// Source address
		System.arraycopy(this.SrcAddress.ToByteArray(true), 0, frame, 7, 7);
		
		// Control bits
		frame[14] = AX25Frame.ControlBits;
		
		// Protocol Identifier
		frame[15] = super.ProtocolIdentifier;
		
		// counter
		frame[16] = this.TCCounter;
		// Information Field
		
		byte[] informationField = this.GetInformationField ();
        System.arraycopy(informationField, 0, frame, 17, informationField.length);
		
		return frame;
	}
}
