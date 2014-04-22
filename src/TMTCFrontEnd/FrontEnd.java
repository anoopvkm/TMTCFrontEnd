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
	
	// constructor
	public FrontEnd() throws FileNotFoundException, UnsupportedEncodingException{
		Trace.WriteLine("Starting TMTC Front End .... ");
		
		_transmitter = new TCTransmitter();
		
		_receiver = new TMReceiver();
		
		
		
		
	}
	
	/**
	 * Function which starts the operations of the TMTC front end
	 * Starts all threads
	 */
	public void Start(){
		// Thread which listens to MCS for packets
		Thread MCSListener = new Thread(new Runnable() {
			public void run(){
				ListenToMCS();
			}
		});
		MCSListener.start();
		Thread ControllerThread = new Thread(new Runnable(){
			public void run(){
				ControlOperations();
			}
		});
		ControllerThread.start();
	}
	/**
	 * function which listens to MCS system 
	 * 
	 */
	private  void ListenToMCS(){
		// TODO 
		// do a test implementation here 
		while(true){
			ApplicationData data = new ApplicationData();
			
			_transmitter.receivePacketTCTransmitter(data);
			break;
		}
	}
	
	/** 
	 * Function which controlls the operations of the TMTC front end
	 */
	private void ControlOperations(){
			
	}
}
