package RouterClient;

import java.util.LinkedList;

import TMTCFrontEnd.MissionConstants;

import AX25.AX25AddressField;
import AX25.AX25FrameIdentification;
import AX25.AX25FrameStatus;
import AX25.AX25Telemetry;
import CRC.CRC16CCITT;

public class Simulator {

	public static LinkedList<Integer> recpacks = new LinkedList<Integer>();
	
	public static AX25Telemetry GetAck(){
		
		AX25AddressField src = new AX25AddressField(MissionConstants.satCallsign,MissionConstants.satSSID);
		AX25AddressField dest = new AX25AddressField(MissionConstants.gsCallsign,MissionConstants.gsSSID);
		byte [] data = new byte[recpacks.size()];

		for(int i =0;i<recpacks.size();i++){
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
	
	public static AX25Telemetry GetPacketDropAck(){
		
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
