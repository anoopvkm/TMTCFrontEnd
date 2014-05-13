package Simulators;
/**
 * This class implements mission control system simulator
 * @author Anoop R Santhosh
 * @version 1.0
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import TMTCFrontEnd.ApplicationData;

public class MCSSimulator {

	// function which sends frame on port 4445
	public void StartSimulation(){
		try {
			
			Socket socket = new Socket("localHost", 4445);
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			
			int i = 0;
			while(i<6){
				i++;
				for(int j =0;j < 6;j++){
					ApplicationData data = new ApplicationData();
					byte[] tem = new byte[100];
					data.SetData(tem);
					outputStream.writeObject(data);
			
					
					
				}
				Thread.sleep(180000);
			}
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// just a verification function.
	public void Secondarypart(){
		try {
		ServerSocket serverSocket = new ServerSocket(4445);
		Socket socket = serverSocket.accept();

		ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
		while(true){
		
			System.out.println(inStream);
			if(inStream != null){
				ApplicationData data = new ApplicationData();
				data = (ApplicationData)inStream.readObject();
				System.out.println("gotcha");
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
	
	
}
