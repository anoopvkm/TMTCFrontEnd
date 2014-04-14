package TCTransmitter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import AX25.AX25AddressField;
import AX25.AX25Telecommand;
import BitOperations.BitOperations;
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
	private HashMap<Integer,AX25Telecommand> OutStandingPacketMap;
	
	// HashMap to store sendcounter
	private HashMap<Integer,Integer> SendCounter;
	
	// the current ACK to be transmitted
	private AX25Telecommand currentAck;
	
	// Enum representing the state of transmitter
	private State state;
	
	// boolean variable true - indicates transmitter is on 
	private boolean TransmitterState;
	
	// Variable to keep track of frame count
	private int frameCounter;
	
	// constructor
	public TCTransmitter(){

		state = State.READY;
		frameCounter = 0;
		EncodedPacketQueue = new LinkedList<AX25Telecommand>();
		ResendPacketQueue = new LinkedList<AX25Telecommand>();
		OutStandingPacketMap = new HashMap<Integer,AX25Telecommand>();
		SendCounter = new HashMap<Integer,Integer>();
		TransmitterState = false;
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
			default    : // TODO a trace statement 
		}
	}
	/**
	 * Function to be called in case of a time out
	 */
	public void TimeOut(){
		
	}
	/**
	 * Function to be called in case of negative ACK
	 * TODO indicate somewhere that packet was send ???
	 */
	public void receiveAck(byte [] acks){

		switch(state){
			case WAIT_FOR_ACK : 	for(int i =0;i<acks.length;i++){
										int counter = BitOperations.UnsignedBytetoInteger8(acks[i]);
										if(OutStandingPacketMap.containsKey(counter)){
											OutStandingPacketMap.remove(counter);
										}
									}
									ResendPacketQueue.clear();
									ResendPacketQueue.addAll(OutStandingPacketMap.values());
									this.state = State.WAIT_FOR_TRANS;
									break;
									
			default : // TODO trace;
		
		}
	
		
	}
	
	/**
	 * Function to indicate transmitter is turned on
	 */
	public void TransmitterON(){
		this.TransmitterState = true;
	}
	
	/**
	 * Function to indicate transmitter is turned off
	 */
	public void TransmitterOFF(){
		this.TransmitterState = false;
	}
	
	/**
	 * Function to dispatch all AX.25 packets
	 */
	private void dispatchPackets(){
		if(TransmitterState){
			// send ack first
			dropToSocket(currentAck.ToByteArray());
			
			// resend packets
			for(int i =0;i<ResendPacketQueue.size();i++){
			
				int Tcounter = ResendPacketQueue.get(i).GetCounter();
				int Val = SendCounter.get(Tcounter);
				SendCounter.remove(Tcounter);
				Val++;
				SendCounter.put(Tcounter, Val);
				OutStandingPacketMap.put(Tcounter, ResendPacketQueue.get(i));
				dropToSocket(ResendPacketQueue.get(i).ToByteArray());
			}
	
			ResendPacketQueue.clear();
			
			// sending new packets
			for(int i =0;i<EncodedPacketQueue.size();i++){
	
				SendCounter.put(EncodedPacketQueue.get(i).GetCounter(), 1);
				OutStandingPacketMap.put(EncodedPacketQueue.get(i).GetCounter(), EncodedPacketQueue.get(i));
				dropToSocket(EncodedPacketQueue.get(i).ToByteArray());
			}
			
			EncodedPacketQueue.clear();
			
			this.state = State.WAIT_FOR_ACK;
		}
	}
	
	/**
	 * Function to drop packets at the socket
	 */
	private void dropToSocket(byte [] frame){
		
	}
}
