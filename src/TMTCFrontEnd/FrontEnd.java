package TMTCFrontEnd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import AX25.AX25AddressField;
import AX25.AX25FrameIdentification;
import AX25.AX25Telemetry;
import CRC.CRC16CCITT;
import RouterClient.Simulator;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import ReplayController.ReplayController;
import RouterClient.RouterClient;
import SQLClient.SQLClient;
import TCTransmitter.TCTransmitter;
import TMReceiver.TMReceiver;
import Trace.Trace;
/**
 * This class deals with entire TMTC frontend framework
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class FrontEnd {

	// Telecommand transmitter
	TCTransmitter _transmitter;
	
	// Telemetry Receiver
	TMReceiver _receiver;
	
	// SQL Client
	
	
	// Router Client
	RouterClient _routerClient;
	
	public boolean TransmitterON ;
	
	public boolean beaconReceived;
	
	ReplayController replayContr;
	
	// temporary variable to indicate if simulator should be on/off
	boolean simulate = true;
	
	// constructor
	public FrontEnd() throws FileNotFoundException, UnsupportedEncodingException{
		
		Trace.WriteLine("[FRONTEND] Starting TMTC Front End .... ");
		
		_transmitter = new TCTransmitter();
		
		_receiver = new TMReceiver();
		
		TransmitterON = true;
		
		beaconReceived = false;
		
		
	}
	
	/**
	 * Function which starts the operations of the TMTC front end
	 * Starts all threads
	 */
	public void Start(){
		
		// Thread which listens to MCS for packets
		Trace.WriteLine("[FRONTEND] Starting listen to MCS Thread");
		
		Thread MCSListener = new Thread(new Runnable() {
			public void run(){
				try {
					ListenToMCS();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		MCSListener.start();
		
		// Thread to listen to ground station
		Trace.WriteLine("[FRONTEND] Starting Listen to GS thread");
		
		Thread GSListener = new Thread(new Runnable() {
			public void run(){
				ListenToGS();
			}
		});
		GSListener.start();
		
		// Thread which handles the main controller
		Trace.WriteLine("[FRONTEND] Starting the frotnend controller thread");
		
		Thread ControllerThread = new Thread(new Runnable(){
			public void run(){
				ControlOperations();
			}
		});
		ControllerThread.start();
		
		// Thread which swictches between transmitter and receiver
		Thread TransmitterThread = new Thread(new Runnable(){
			public void run(){
				try {
					TransmitterSwitch();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		TransmitterThread.start();

		// Thread which handles the replay controller
		Trace.WriteLine("[FRONTEND] Starting the replay controller");
		Thread ReplayControllerThread = new Thread(new Runnable() {
			public void run(){
				replayContr = new ReplayController();
				replayContr.Display();
			}
		});
		ReplayControllerThread.start();
		
		
		_receiver.start();
	}
	/**
	 * Function which listens to MCS system on port 4445
	 * @throws InterruptedException 
	 * 
	 */
	private  void ListenToMCS() throws InterruptedException{
	
		
		// Simulation
		// listens to MCS on  a socket 
		try {
			ServerSocket serverSocket = new ServerSocket(4445);
			Socket socket = serverSocket.accept();
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			while(true){
				
				if(inStream != null){
					ApplicationData data = new ApplicationData();
					data = (ApplicationData)inStream.readObject();
					_transmitter.receivePacketTCTransmitter(data);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Function to listen to GS on a socket on port 4446
	 * 
	 */
	private void ListenToGS(){
		// Simulation
		try {
			ServerSocket serverSocket = new ServerSocket(4446);
			Socket socket = serverSocket.accept();
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			while(true){
						
				if(inStream != null){
					ByteArray data = new ByteArray();
					data = (ByteArray)inStream.readObject();
					_receiver.receivedFrames.add(data);
				}
			}
		} catch (IOException e) {
					// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
			
		
	}
	
	/** 
	 * Function which controls the operations of the TMTC front end
	 */
	private void ControlOperations(){
		
			// an infinite loop which checks various flags and passes messages from receiver to transmitter. 
			while(true){
				
				if(_receiver.AckReceived){
					_receiver.AckReceived = false;
					_transmitter.receiveAck(_receiver.curAck.Data);
				}
				
				if(this.TransmitterON){
			
					if(_receiver.AckReceived){
						_receiver.AckReceived = false;
						_transmitter.receiveAck(_receiver.curAck.Data);
					}
					_transmitter.recvFrames.addAll(_receiver.ReceivedPacketCounters);
					_receiver.ReceivedPacketCounters.clear();
					
					_transmitter.TransmitterON();
					
				}
				else{
					_transmitter.TransmitterOFF();
					continue;
				}
				
				
				while(this.TransmitterON){
					if(_receiver.AckReceived){
						_receiver.AckReceived = false;
						_transmitter.receiveAck(_receiver.curAck.Data);
					}
					
					if(this.beaconReceived){
						System.out.print("Done");
						this.beaconReceived = false;
						_transmitter.positiveBeacon();
						
					}
					
				}
			}
	}
	
	// properly timed simulator. Unused as of now. 
	// Left behind for easy future testing
	// simulates operations of GS / satellite
	public void Simulator1() throws InterruptedException{
	
		Thread.sleep(100);
		int  i = 0 ;
		int count = 0;
		simulate = true;
		while(i < 2){
			i++;
			System.out.println("new cycle");
			TransmitterON = true;
			
			beaconReceived = true;
			
			Thread.sleep(60000);
			
	//		System.out.println("SwitchHappening");
			TransmitterON = false;
			beaconReceived = false;
			
			// generating acknowledgement
			ByteArray temp = new ByteArray();
			temp.data = Simulator.GetAck().ToByteArray();
			_receiver.receivedFrames.add(temp);
			
		//	System.out.println("AckReceived");
			// generating packets
			for(int j =0;j <40;j++){
				AX25AddressField src = new AX25AddressField(MissionConstants.satCallsign,MissionConstants.satSSID);
				AX25AddressField dest = new AX25AddressField(MissionConstants.gsCallsign,MissionConstants.gsSSID);
				byte [] data = new byte[40];
			
				AX25Telemetry temp1 = new AX25Telemetry(dest, src, new AX25FrameIdentification(), (byte)count, (byte)count,(byte)0xFE,  data, null, 10);
				byte [] crc = new byte[2];
				crc = CRC16CCITT.generateCRC(temp1.ToByteArrayWithoutCRC());
				temp1._crc[0] = crc[0];
				temp1._crc[1] = crc[1];
				temp = new ByteArray();
				temp.data = temp1.ToByteArray();
				_receiver.receivedFrames.add(temp);
		
				System.out.println(count);
				count = ( count+1 ) %256;
				
			}
		
			 Thread.sleep(180000);
			
		}
	}
	
	/**
	 * Function to alternatively turn on and off transmitter
	 * @throws InterruptedException 
	 */
	public void TransmitterSwitch() throws InterruptedException{
		// transmitter is switched on for 1 min
		// transmitter is switched off for 3 min
		while(true){
			TransmitterON = true;
			beaconReceived = true;
			Thread.sleep(60000);
			TransmitterON= false;
			beaconReceived = false;
			Thread.sleep(180000);
		}
	}
}
