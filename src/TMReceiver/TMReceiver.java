package TMReceiver;

import java.util.LinkedList;

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
	private LinkedList<Integer> ReceivedPacketCounters;
	
	public TMReceiver(){
		this.DecodedPackets = new LinkedList<AX25Telemetry>();
		this.ReceivedPacketCounters = new LinkedList<Integer>();
	}
	/**
	 * Function to receive a new telemetry packet and decode it and add it to the queue
	 * @param frame
	 */
	public void receivePacket(byte [] frame){
		AX25Telemetry Frame = new AX25Telemetry(frame);
		DecodedPackets.add(Frame);
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
	
	private void ArchieveTelemetry(byte [] frame){
		// TODO write to sql database
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
	 */
	private void addToAckQueue(AX25Telemetry frame){
		int counter = BitOperations.UnsignedBytetoInteger8(frame.MasterFrameCount);
		ReceivedPacketCounters.add(counter);
	}
}
