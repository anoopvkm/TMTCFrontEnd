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
	SQLClient _sqlClient;
	
	// Router Client
	RouterClient _routerClient;
	
	// constructor
	public FrontEnd() throws FileNotFoundException, UnsupportedEncodingException{
		Trace.WriteLine("Starting TMTC Front End .... ");
		
		_transmitter = new TCTransmitter();
		
		_receiver = new TMReceiver();
		
		
	}
	
	
}
