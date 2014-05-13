package Simulators;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import AX25.AX25AddressField;
import AX25.AX25FrameIdentification;
import AX25.AX25FrameStatus;
import AX25.AX25Telecommand;
import AX25.AX25Telemetry;
import CRC.CRC16CCITT;
import RouterClient.Simulator;
import TMTCFrontEnd.ApplicationData;
import TMTCFrontEnd.ByteArray;
import TMTCFrontEnd.MissionConstants;
/**
 * This class implements ground station simulator
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class GSSimulator {

	public  LinkedList<Integer> recpacks = new LinkedList<Integer>();
	
	// function which listens to TMTC Frontend on socket 4447
	public void ListenToTMTC(){
		try {
			ServerSocket serverSocket = new ServerSocket(4447);
			Socket socket = serverSocket.accept();
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			while(true){
				
				if(inStream != null){
					ByteArray Data = new ByteArray();
					Data = (ByteArray)inStream.readObject();
					AX25Telecommand Frame = new AX25Telecommand(Data.data);
					recpacks.add(Frame.GetCounter());
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
	
	// function which sends frames to TMTC frontend on port 4446
	public void SendToTMTC(){
		Socket socket;
		try {
			socket = new Socket("localHost", 4446);
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			int count = 0;
			for(int i =0 ;i <3;i++){
				Thread.sleep(60100);
				ByteArray temp = new ByteArray();
				temp.data = GetAck().ToByteArray();
				outputStream.writeObject(temp);
				
				for(int j =1;j <40;j++){
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
					outputStream.writeObject(temp);
			
	
					count = ( count+1 ) %256;
					
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
	
	// function to get acknowledgement frame
	public  AX25Telemetry GetAck(){
		
		AX25AddressField src = new AX25AddressField(MissionConstants.satCallsign,MissionConstants.satSSID);
		AX25AddressField dest = new AX25AddressField(MissionConstants.gsCallsign,MissionConstants.gsSSID);
		byte [] data;
		if(recpacks.size() == 0){
			data = new byte[0];
		}
		else
			data = new byte[recpacks.size()-1];

		for(int i =1;i<recpacks.size();i++){
			data[i-1] = BitOperations.BitOperations.IntegerToUnsignedbyte8(recpacks.get(i).intValue());
		}
		recpacks.clear();
		AX25Telemetry temp = new AX25Telemetry(dest, src, new AX25FrameIdentification(), (byte)0, (byte)0,(byte)0,  data, new AX25FrameStatus(), 10);
		temp.ProtocolIdentifier = 0x03;
	
		
		byte [] crc = new byte[2];
		crc = CRC16CCITT.generateCRC(temp.ToByteArrayWithoutCRC());
		temp._crc[0] = crc[0];
		temp._crc[1] = crc[1];
		
		
		
		
		
		
		return temp;
	}
	
	public  AX25Telemetry GetPacketDropAck(){
		
		AX25AddressField src = new AX25AddressField(MissionConstants.satCallsign,MissionConstants.satSSID);
		AX25AddressField dest = new AX25AddressField(MissionConstants.gsCallsign,MissionConstants.gsSSID);
		
		byte [] data = new byte[recpacks.size() - 2];

		for(int i =0;i<recpacks.size() - 2;i++){
	
			data[i] = BitOperations.BitOperations.IntegerToUnsignedbyte8(recpacks.get(i).intValue());
		}
		recpacks.clear();
		AX25Telemetry temp = new AX25Telemetry(dest, src, new AX25FrameIdentification(), (byte)0, (byte)0,(byte)0,  data, new AX25FrameStatus(), 10);
		temp.ProtocolIdentifier = 0x03;
	
		
		byte [] crc = new byte[2];
		crc = CRC16CCITT.generateCRC(temp.ToByteArrayWithoutCRC());
		temp._crc[0] = crc[0];
		temp._crc[1] = crc[1];
		
		
		
		
		
		
		return temp;
	}
}
