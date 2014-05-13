package AX25;

import Trace.Trace;

/**
 * This class deals with the entire AX.25 frame
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class AX25Frame {

	//  Destination address
	public AX25AddressField DstAddress;
	
	// Source Address
	public AX25AddressField SrcAddress;
	
	// Control Bits
	protected static byte ControlBits = 0x03;
	
	// Protocol Identifier (no layer 3 protocol implemented )
	public  byte ProtocolIdentifier = (byte)0xF0; // standard value // this has been changed to implement ack option
	
	// Information field
	public byte[] _informationField;
	
	// CRC field
	public byte[] _crc;
	
	// Header Length in bytes
	public static int HeaderLength = 16;
	
	/**
	 * AX.25 frame class constructor
	 */
	public AX25Frame(){
		this.DstAddress =  new AX25AddressField();
		this.SrcAddress = new AX25AddressField();
		this.SetInformationField (new byte[0]);
		this._crc = new byte[2];
	}
	
	/**
	 * AX.25 frame constructor
	 * @param frame byte array of frame bytes
	 */
	public AX25Frame(byte[] frame){
		this(frame,0);
	}
	
	/**
	 * AX.25 frame constructor given bytes and offset
	 * @param frame AX.25 frame
	 * @param offset into the data byte array
	 */
	public AX25Frame(byte[] frame,int offset){
		this();
		this.DstAddress = new AX25AddressField(frame,offset);
		this.SrcAddress = new AX25AddressField(frame,offset + 7);
		this._crc = new byte[2];
		// checking for control bits
		if(frame[offset + 15 ] != AX25Frame.ControlBits){
			Trace.WriteLine("[AX25FRAME] Mismatch in Control Bits");
		}
		
		
		// the protocol identifier field has been modified to represent if the packet is an ack packet or not
		this.ProtocolIdentifier = frame[offset+15];
		
		// information field
		if(frame.length - offset - AX25Frame.HeaderLength > 0){
			byte[] informationField = new byte[frame.length - offset -2 - AX25Frame.HeaderLength];
			System.arraycopy (frame, (int)(offset + AX25Frame.HeaderLength), informationField, 0, (int)(frame.length - offset - 2 - AX25Frame.HeaderLength));
			
			this.SetInformationField (informationField);
			this._crc = new byte[2];
			System.arraycopy(frame, (int)(offset+AX25Frame.HeaderLength + informationField.length), this._crc, 0, 2);
		}
		else{
			this.SetInformationField (new byte[0]);
		}
	}
	
	/**
	 * AX.25 frame constructor
	 * @param dstAddress destination address
	 * @param srcAddress source address
	 * @param informationField information field in an array of bytes
	 */
	 public AX25Frame(AX25AddressField dstAddress, AX25AddressField srcAddress, byte[] informationField){
          this ();
          this.DstAddress = dstAddress;
          this.SrcAddress = srcAddress;
          this.SetInformationField (informationField);
          this._crc = new byte[2];
     }
	 
	/**
	  * This  method sets the protocol identifier field - for using to identify packet type
	  * @param type  a byte to set the protocol identifier field
	  */
	 public void SetIdentifier(byte type){
		 this.ProtocolIdentifier = type;
	 }
	/**
	 * This method gives the total length of the frame
	 * @param 
	 * @return int length of the frame
	 */
	public int getFrameLength(){
		return (int)(AX25Frame.HeaderLength + this.GetInformationField().length);
	}
	
	/**
	 * This method converts the frame to a byte array
	 * @return  byte[] array representation of the frame
	 */
	public byte[] ToByteArray() {
		byte[] frame = new byte[this.getFrameLength()+2];
		
		// destination address field
		System.arraycopy(this.DstAddress.ToByteArray(false), 0, frame, 0, 7);
		
		// Source address
		System.arraycopy(this.SrcAddress.ToByteArray(true), 0, frame, 7, 7);
		
		// Control bits
		frame[14] = AX25Frame.ControlBits;
		
		// Protocol Identifier
		frame[15] = this.ProtocolIdentifier;
		
		// Information Field
		byte[] informationField = this.GetInformationField ();
        System.arraycopy(informationField, 0, frame, 16, informationField.length);
		
        // CRC
        System.arraycopy(_crc, 0, frame,this.getFrameLength() , 2);
        
		return frame;
	}
	
	/** This method converts the frame to a byte array with out crc field
	 * @return byte[] array representation of the frame without crc
	 */
	public byte[] ToByteArrayWithoutCRC() {
		byte[] frame = new byte[this.getFrameLength()];
		
		// destination address field
		System.arraycopy(this.DstAddress.ToByteArray(false), 0, frame, 0, 7);
		
		// Source address
		System.arraycopy(this.SrcAddress.ToByteArray(true), 0, frame, 7, 7);
		
		// Control bits
		frame[14] = AX25Frame.ControlBits;
		
		// Protocol Identifier
		frame[15] = this.ProtocolIdentifier;
		
		// Information Field
		byte[] informationField = this.GetInformationField ();
        System.arraycopy(informationField, 0, frame, 16, informationField.length);
		
		return frame;
	}
	
	/**
	 * This method returns the information field of the byte array
	 * @return byte[] information field of the frame
	 */
	protected byte[] GetInformationField() {
         if (this._informationField == null){
              return new byte[0];
         }
         else{
              return this._informationField;
         }
    }
	
	/**
	 * This method sets the information field of the frame
	 * @param informationField a byte array of information field values
	 */
	public void SetInformationField(byte[] informationField){
         this._informationField = informationField;
    }
	
}
