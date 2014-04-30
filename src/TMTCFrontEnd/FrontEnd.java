package TMTCFrontEnd;

import java.io.FileNotFoundException;

import AX25.AX25AddressField;
import AX25.AX25FrameIdentification;
import AX25.AX25Telemetry;
import CRC.CRC16CCITT;
import RouterClient.Simulator;
import java.io.UnsupportedEncodingException;

import ReplayController.ReplayController;
import RouterClient.RouterClient;
import SQLClient.SQLClient;
import TCTransmitter.TCTransmitter;
import TMReceiver.ByteArray;
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
		Trace.WriteLine("Starting TMTC Front End .... ");
		
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
		Thread ControllerThread = new Thread(new Runnable(){
			public void run(){
				ControlOperations();
			}
		});
		ControllerThread.start();

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
	 * function which listens to MCS system 
	 * @throws InterruptedException 
	 * 
	 */
	private  void ListenToMCS() throws InterruptedException{
		// TODO 
		// do a test implementation here 
		
	
		
		// Simulation
			int i = 0;
			while(i<6){
				i++;
				for(int j =0;j < 6;j++){
					ApplicationData data = new ApplicationData();
					byte[] tem = new byte[100];
					data.SetData(tem);
					_transmitter.receivePacketTCTransmitter(data);
				}
				Thread.sleep(180000);
			}
		
	}
	
	/** 
	 * Function which controlls the operations of the TMTC front end
	 */
	private void ControlOperations(){
			while(true){
				
				
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
					if(this.beaconReceived){
						System.out.print("Done");
						this.beaconReceived = false;
						_transmitter.positiveBeacon();
						
					}
					
				}
			}
	}
	// properly timed simulator
	
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
			
			ByteArray temp = new ByteArray();
			temp.data = Simulator.GetAck().ToByteArray();
			_receiver.receivedFrames.add(temp);
			
		//	System.out.println("AckReceived");
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
		while(true){
			TransmitterON = true;
			Thread.sleep(1000);
			TransmitterON= false;
			Thread.sleep(2000);
		}
	}
}
