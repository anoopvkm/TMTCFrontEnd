package TCTransmitter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import AX25.AX25AddressField;
import AX25.AX25Telecommand;
import BitOperations.BitOperations;
import CRC.CRC16CCITT;
import RouterClient.Simulator;
import SQLClient.SQLClient;
import TMTCFrontEnd.ApplicationData;
import TMTCFrontEnd.ByteArray;
import TMTCFrontEnd.MissionConstants;

/**
 * This class deals with entire TC Transmitter
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class TCTransmitter {

	// queue to store packets coming from MCS
	private Queue<ApplicationData> IncomingPacketQueue ;
	
	// queue for storing encoded AX.25 telecommand packets
	private LinkedBlockingQueue<AX25Telecommand> EncodedPacketQueue;
	
	// queue for storing 
	private LinkedBlockingQueue<AX25Telecommand> ResendPacketQueue;
	
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
	
	// To archieve AX25 frames
	SQLClient _sqlClient ; 
	
	// packets received by the receiver
	public LinkedBlockingQueue<Integer> recvFrames;

	
	// flag to indicate if the current Ack is valid
	private boolean validAck;
	// UI to display receiver messages
	JFrame msgFrame ;
	JTextArea msg;
	
	// socket connection
	Socket socket = null;
	ObjectOutputStream outputStream = null;
	public TCTransmitter(){

		state = State.WAIT_FOR_TRANS;
		frameCounter = 0;
		EncodedPacketQueue = new LinkedBlockingQueue<AX25Telecommand>();
		ResendPacketQueue = new LinkedBlockingQueue<AX25Telecommand>();
		OutStandingPacketMap = new HashMap<Integer,AX25Telecommand>();
		SendCounter = new HashMap<Integer,Integer>();
		TransmitterState = false;
		_sqlClient = new SQLClient();
		recvFrames = new LinkedBlockingQueue<Integer>();
		validAck = false;
		
		// for temporary dialog box
		msgFrame = new JFrame("Transmitter Messages ");
		msg = new JTextArea();
		msg.setColumns(70);
		msg.setRows(40);
		msgFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        msgFrame.setSize(350,300);
        msgFrame.add(msg);
        msgFrame.setVisible(true);
        
        // for socket connection
        Socket socket;
		try {
			socket = new Socket("localHost", 4447);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
        
        
	}
	
	/*
	 * Function to receives data, encodes it without counter and stores it into the encoded queue
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
		AX25Telecommand EncodedPacket =  new AX25Telecommand(dstAddress,srcAddress,data.GetData(),frameCounter);
		
		frameCounter = (frameCounter+1)%256;
		
		byte [] crc = new byte[2];
		crc = CRC16CCITT.generateCRC(EncodedPacket.ToByteArrayWithoutCRC());
		System.arraycopy(crc, 0, EncodedPacket._crc, 0, 2);
		
		_sqlClient.ArchieveAX25TeleCommand(EncodedPacket);
		
		
		// Adding the packet to encoded packets Queue
		this.EncodedPacketQueue.add(EncodedPacket);
	
		
		
	}
	/**
	 * Function to start the operations of a receiver
	 */
	public void start(){
		
	}
	/**
	 * Function to be called when a positive beacon has been received indicating transmission can be done
	 */
	public void positiveBeacon(){
		
		switch(state){
			case READY : 
						dispatchPackets();
						break; 
			default    : // TODO a trace statement 
		}
	}
	/**
	 * Function to be called in case of a time out
	 */
	public void TimeOut(){
		switch(state){
			case WAIT_FOR_ACK :	
									ResendPacketQueue.addAll(OutStandingPacketMap.values());
									OutStandingPacketMap.clear();
									this.state = State.WAIT_FOR_TRANS;
									break;
									
			default : // TODO tracedc
									
		}
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
								
									ResendPacketQueue.addAll(OutStandingPacketMap.values());
									OutStandingPacketMap.clear();
									AnnounceMessage("Ack received. Going to WAIT_FOR_TRANS state");
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
	
	
		switch(state){
			case WAIT_FOR_ACK :		ResendPacketQueue.clear();
									ResendPacketQueue.addAll(OutStandingPacketMap.values());
									ConstructAck(); // function called to construct ack
									AnnounceMessage("Transmitter Switched ON. Going to READY state");
									this.state = State.READY;
									break;
									
			case WAIT_FOR_TRANS:  	ConstructAck();
									AnnounceMessage("Transmitter Switched ON. Going to READY state");
									this.state = State.READY;
									break;
			default : // TODO 
		}
	}
	
	/**
	 * Function to indicate transmitter is turned off
	 */
	public void TransmitterOFF(){
		this.TransmitterState = false;
		switch(state){
			case READY : // stop the dispatch thread ?
						this.state = State.WAIT_FOR_ACK;
						break;
			default    : //TODO 
		}
	}
	
	/**
	 * Function to construct ack for packets received for them to transmitted
	 */
	private void ConstructAck(){
		byte [] acks = new byte[recvFrames.size()];
		int  i =0;
		while(i < acks.length){
			acks[i] = BitOperations.IntegerToUnsignedbyte8(recvFrames.poll());
			i++;
		}
		currentAck = new AX25Telecommand();
		currentAck.SetInformationField(acks);
		currentAck.ProtocolIdentifier = 0x03;
		recvFrames.clear();
		validAck = true;
		
	}
	/**
	 * Function to dispatch all AX.25 packets
	 */
	private void dispatchPackets(){
		
		// send ack first
		if(validAck){
			validAck = false;
			
			dropToSocket(currentAck);
		}
		//to hold the packets which cannot be send bcoz another packet of same counter was sent already
		LinkedList<AX25Telecommand> HoldList = new LinkedList<AX25Telecommand>();
		
		// resend packets
		while(!ResendPacketQueue.isEmpty()){
			
			AX25Telecommand Frame = ResendPacketQueue.poll();
			int Tcounter = Frame.GetCounter();
			if(OutStandingPacketMap.containsKey(Tcounter)){
				HoldList.add(Frame);
				continue;
			}
			int Val = SendCounter.get(Tcounter);
			SendCounter.remove(Tcounter);
			if( Val >= MissionConstants.MaxResends){
				AnnounceMessage("Packet with counter "+Tcounter+" dropped after Max. Resends");
				continue;
			}
			Val++;
			SendCounter.put(Tcounter, Val);
			OutStandingPacketMap.put(Tcounter, Frame);
			dropToSocket(Frame);
		}
		ResendPacketQueue.addAll(HoldList);
		HoldList.clear();
		

		// sending new packets
		while(!EncodedPacketQueue.isEmpty()){

			AX25Telecommand Frame = EncodedPacketQueue.poll();
			if(OutStandingPacketMap.containsKey(Frame.GetCounter())){
				HoldList.add(Frame);
				continue;
			}
			SendCounter.put(Frame.GetCounter(), 1);
		
			OutStandingPacketMap.put(Frame.GetCounter(), Frame);
			dropToSocket(Frame);
		}
			
		EncodedPacketQueue.addAll(HoldList);
		HoldList.clear();
			
		AnnounceMessage("Packets Dispatched. Going to WAIT_FOR_ACK state");
		this.state = State.WAIT_FOR_ACK;
		
	}
	
	/**
	 * Function to drop packets at the socket on port 4457
	 * @param Frame AX25Telecommand frame
	 */
	private void dropToSocket(AX25Telecommand Frame){
		// TODO
		if(Frame.ProtocolIdentifier == 0x03){
			AnnounceMessage("Ack ");
		}
		else{
			AnnounceMessage("PacketSend "+" "+BitOperations.UnsignedBytetoInteger8(Frame.TCCounter));
		
			// Dropping to socket
			ByteArray temp = new ByteArray();
			temp.data = Frame.ToByteArray();
			try {
				outputStream.writeObject(temp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Function to print message in window
	 */
	private void AnnounceMessage(String errmsg){
		msg.append("\n"+errmsg);
	//	System.out.println(errmsg);
	}
}
