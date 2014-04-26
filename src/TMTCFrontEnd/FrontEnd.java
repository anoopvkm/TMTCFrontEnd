package TMTCFrontEnd;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

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
	
	// constructor
	public FrontEnd() throws FileNotFoundException, UnsupportedEncodingException{
		Trace.WriteLine("Starting TMTC Front End .... ");
		
		_transmitter = new TCTransmitter();
		
		_receiver = new TMReceiver();
		
		TransmitterON = false;
		
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
		while(true){
			ApplicationData data = new ApplicationData();
			byte[] tem = new byte[4];
			data.SetData(tem);
			_transmitter.receivePacketTCTransmitter(data);
			Thread.sleep(200);
		}
	}
	
	/** 
	 * Function which controlls the operations of the TMTC front end
	 */
	private void ControlOperations(){
			while(true){
				if(_receiver.AckReceived){
					_receiver.AckReceived = false;
					_transmitter.receiveAck(_receiver.curAck.Data);
				}
				
				if(this.TransmitterON){
					_transmitter.recvFrames.addAll(_receiver.ReceivedPacketCounters);
					_receiver.ReceivedPacketCounters.clear();
					
					_transmitter.TransmitterON();
				}
				else{
					_transmitter.TransmitterOFF();
				}
				if(this.beaconReceived){
			
					this.beaconReceived = false;
					_transmitter.positiveBeacon();
					
				}
				
				while(this.TransmitterON); 
			}
	}
	
	public void Simulator() throws InterruptedException{
		//Thread.sleep(300);
		TransmitterON = true;
		beaconReceived = true;
	
	//	TransmitterON = false;
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
