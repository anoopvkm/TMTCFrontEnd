package TMReceiver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import SQLClient.SQLClient;
import TMTCFrontEnd.MissionConstants;

import AX25.AX25Telemetry;
import BitOperations.BitOperations;
import CRC.CRC16CCITT;

/**
 * This class deals with Receiver part
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class TMReceiver {

	// to store the list of byte stream objects which has been Decoded into AX25 Telemtery 
	private LinkedList<AX25Telemetry> DecodedPackets ;
	
	// to store the counters of the packets received
	public LinkedBlockingQueue<Integer> ReceivedPacketCounters;
	
	// to store all reassembly units
	private ArrayList<ReAssemblyUnit> ReAssemblyUnitList;
	
	// to store all the received bytearrays
	private LinkedBlockingQueue<ByteArray> receivedFrames;
	
	// SQL client to archieve telemetry
	private SQLClient _sqlClient ;
	
	// Currently received ACK
	public AX25Telemetry curAck ;
	
	// to indicate that an ack was received
	public boolean AckReceived ;
	
	public TMReceiver(){
		this.DecodedPackets = new LinkedList<AX25Telemetry>();
		this.ReceivedPacketCounters = new LinkedBlockingQueue<Integer>();
		this.ReAssemblyUnitList = new ArrayList<ReAssemblyUnit>();
		for(int i =0 ;i< MissionConstants.NoVCs ;i++){
			ReAssemblyUnit temp = new ReAssemblyUnit(i);
			ReAssemblyUnitList.add(temp);
		}
		receivedFrames = new LinkedBlockingQueue<ByteArray>();
		_sqlClient = new SQLClient();
		AckReceived = false;
	}
	/**
	 * Function to accept a new telemetry packet and decode it and add it to the queue
	 * @param frame
	 */
	public AX25Telemetry acceptPacket(byte [] frame){
		AX25Telemetry Frame = new AX25Telemetry(frame);
		return Frame;
	}
	
	/**
	 * Function to do a crc check on the packet
	 * returns true if the packet is valid
	 */
	private boolean checkCRC(byte [] frame ){
		byte [] crc = CRC16CCITT.generateCRC(frame);
		if(crc[0] == 0 && crc[1] == 0){
			return true;
		}
		return false;
	}
	
	/**
	 * Function to listen to GS on a socket
	 * 
	 */
	private void ListenToGS(){
		// TODO write the socket code here
		while(true){
			// TODO add it to incoming telemetryqueue.
		}
	}
	
	/**
	 * Function which does all operations on the received packets
	 */
	private void ReceiverController(){
		while(true){
			
			if(!receivedFrames.isEmpty()){
				ByteArray frame = receivedFrames.poll();
				if(!checkCRC(frame.data)){
					// TODO trace
					continue;
				}
				final AX25Telemetry DecodedFrame = acceptPacket(frame.data);
				
				if(!checkSRCandDESTAddr(DecodedFrame)){
					// TODO trace
					continue;
				}
				
				ArchieveTelemetry(DecodedFrame);
				
				if(DecodedFrame.ProtocolIdentifier == 0x03){
					curAck = DecodedFrame;
					break;
				}
				
				addToAckQueue(DecodedFrame);
				
				Thread VCsplitThread = new Thread (new Runnable () {
					public void run(){
						SplitToVirtualChannels(DecodedFrame);
					}
				});
				VCsplitThread.start();
				
				
			}
		}
	}
	
	/**
	 * Function which starts all the threads in receiver part
	 * 
	 */
	public void start(){
		Thread GSListenerThread = new Thread (new Runnable () {
			public void run(){
				ListenToGS();
			}
		});
		GSListenerThread.start();
		Thread ReceiverMainThread = new Thread ( new Runnable () {
			public void run(){
				ReceiverController();
			}
		});
		ReceiverMainThread.start();
		
	}
	
	/**
	 * Function to archieve a telemetry packet
	 * @param Frame
	 */
	private void ArchieveTelemetry(AX25Telemetry Frame){
		_sqlClient.ArchieveAX25TeleMetry(Frame);
	} 
	
	/**
	 * Function to check the satellite and groundsattion address 
	 * @param frame
	 * @return true when valid address
	 */
	private boolean checkSRCandDESTAddr(AX25Telemetry frame){
		boolean satellitecheck = false;
		boolean groundstationcheck = false;
		// check the satellite - source in this case
		if(frame.SrcAddress.CallSign.equals(MissionConstants.satCallsign) && (frame.SrcAddress.Ssid == MissionConstants.satSSID)){
			satellitecheck = true;
		}
		if(frame.DstAddress.CallSign.equals(MissionConstants.gsCallsign) && (frame.DstAddress.Ssid == MissionConstants.gsSSID)){
			groundstationcheck = true;
		}
		
		return satellitecheck && groundstationcheck;
	}
	
	/**
	 * Function to add counters to the list of received packet coutners 
	 * @param AX25 telemetry
	 */
	private void addToAckQueue(AX25Telemetry frame){
		int counter = BitOperations.UnsignedBytetoInteger8(frame.MasterFrameCount);
		ReceivedPacketCounters.add(counter);
	}
	
	/**
	 * Function to split to different virtual channels and and called Reassembly unit
	 * @param Ax25telemetry
	 */
	private void SplitToVirtualChannels(AX25Telemetry frame){
		int vcid = BitOperations.UnsignedBytetoInteger8(frame.FrameIdentification.VirtualChannelID);
		ReAssemblyUnitList.get(vcid).ReassemblePacket(frame);
	}
	
	/**
	 * Function to print message in window
	 */
	private void AnnouncePacketDrop(String errmsg){
		// TODO dialog box with message
	}
}
