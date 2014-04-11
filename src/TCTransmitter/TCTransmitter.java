package TCTransmitter;

import java.util.LinkedList;
import java.util.Queue;

import AX25.AX25AddressField;
import AX25.AX25Telecommand;
import TMTCFrontEnd.ApplicationData;

/**
 * This class deals with entire TC Transmitter
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class TCTransmitter {

	// queue to store packets coming from MCS
	private Queue<ApplicationData> IncomingPacketQueue ;
	
	// queue for storing encoded AX.25 telecommand packets
	private Queue<AX25Telecommand> EncodedPacketQueue;
	

	
	
	
	// constructor
	public TCTransmitter(){

		EncodedPacketQueue = new LinkedList<AX25Telecommand>();
	}
	
	/*
	 * Function to receives data, encodes it wiithout counter and stores it into the encoded queue
	 * @param data, an application data object
	 */
	public void receivePacketTCTransmitter(ApplicationData data){
		this.ProtocolEncoding(data);
	}
	
	/*
	 * Function which takes an input and encodes it into the protocol we want
	 * @param data
	 */
	public void ProtocolEncoding(ApplicationData data){
		
		// destination address field
		AX25AddressField dstAddress  = new AX25AddressField(); // TODO call with params
		
		// Source address field
		AX25AddressField srcAddress  = new AX25AddressField(); // TODO call with params
		
		// Encoding the data to AX.25 protocol
		AX25Telecommand EncodedPacket =  new AX25Telecommand(dstAddress,srcAddress,data.GetData());
		
		// Adding the packet to encoded packets Queue
		this.EncodedPacketQueue.add(EncodedPacket);
		
		
		
	}
}
