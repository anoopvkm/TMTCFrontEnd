package TCTransmitter;

import java.util.HashMap;
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
	private LinkedList<AX25Telecommand> EncodedPacketQueue;
	
	// queue for storing 
	private LinkedList<AX25Telecommand> ResendPacketQueue;
	
	// outstanding Packet queue
	private LinkedList<AX25Telecommand> OutStandingPacketQueue;
	
	// HashMap to store sendcounter
	HashMap<Integer,Integer> SendCounter;
	
	// the current ACK to be transmitted
	AX25Telecommand currentAck;
	// Enum representing the state of transmitter
	State state;
	
	// Variable to keep track of frame count
	int frameCounter;
	
	// constructor
	public TCTransmitter(){

		state = State.READY;
		frameCounter = 0;
		EncodedPacketQueue = new LinkedList<AX25Telecommand>();
		ResendPacketQueue = new LinkedList<AX25Telecommand>();
		OutStandingPacketQueue = new LinkedList<AX25Telecommand>();
		SendCounter = new HashMap<Integer,Integer>();
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
		
		frameCounter = (frameCounter+1)%256;
		// Encoding the data to AX.25 protocol
		AX25Telecommand EncodedPacket =  new AX25Telecommand(dstAddress,srcAddress,data.GetData(),frameCounter);
		
		// TODO write SQL function to write to DB
		
		// Adding the packet to encoded packets Queue
		this.EncodedPacketQueue.add(EncodedPacket);
		
		
		
	}
	/**
	 * Function to be called when a positive beacon has been received indicating transmission can be done
	 */
	public void positiveBeacon(){
		switch(state){
			case READY : dispatchPackets();
						 break; 
		}
	}
	/**
	 * Function to be called in case of a time out
	 */
	public void TimeOut(){
		
	}
	/**
	 * Function to be called in case of negative ACK
	 * 
	 */
	public void receiveNegativeAck(){
		
	}
	/**
	 * Function to be called all packets are received
	 */
	public void allReceived(){
		
	}
	/**
	 * Function to dispatch all AX.25 packets
	 */
	private void dispatchPackets(){
		// send ack first
		dropToSocket(currentAck.ToByteArray());
		
		// resend packets
		for(int i =0;i<ResendPacketQueue.size();i++){
			dropToSocket(ResendPacketQueue.get(i).ToByteArray());
			SendCounter.get(ResendPacketQueue.get(i).GetCounter())++;
		}
		OutStandingPacketQueue.addAll(ResendPacketQueue);
		ResendPacketQueue.clear();
		
		// sending new packets
		for(int i =0;i<EncodedPacketQueue.size();i++){
			dropToSocket(EncodedPacketQueue.get(i).ToByteArray());
		}
		OutStandingPacketQueue.addAll(EncodedPacketQueue);
		EncodedPacketQueue.clear();
	}
	
	/**
	 * Function to drop packets at the socket
	 */
	private void dropToSocket(byte [] frame){
		
	}
}
