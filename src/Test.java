import java.awt.FlowLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.mysql.jdbc.Blob;

import ReplayController.ReplayController;
import SQLClient.SQLClient;
import Simulators.GSSimulator;
import Simulators.MCSSimulator;
import TMReceiver.ReAssemblyUnit;
import TMTCFrontEnd.FrontEnd;
import TMTCFrontEnd.MissionConstants;
import Trace.Trace;
import AX25.AX25AddressField;
import AX25.AX25FrameIdentification;
import AX25.AX25FrameStatus;
import AX25.AX25Telemetry;
import BitOperations.BitOperations;
import CRC.CRC16CCITT;


public class Test {

	  public static void main(String a[]) throws SQLException, InterruptedException, IOException {
		  Trace.SetTraceFile("/home/anoop/blah");
		

		 
		 final MCSSimulator mcsSim = new MCSSimulator();
		 final GSSimulator gsSim = new GSSimulator();
		 
	
		 Thread GSSim1 = new Thread(new Runnable(){
				public void run(){
					
						gsSim.ListenToTMTC();
					
				}
			});
		 GSSim1.start();
		
		 final FrontEnd test = new FrontEnd();
		 test.Start();
		
		 Thread GSSim2 = new Thread(new Runnable(){
				public void run(){
					
						gsSim.SendToTMTC();
					
				}
			});
		GSSim2.start();
		
		 Thread MCSSim = new Thread(new Runnable(){
				public void run(){
					mcsSim.StartSimulation();
				}
			});
		MCSSim.start();
		// test.Simulator1();
		
		//  MakeTelemetryEntries();
		  //ReassemblyUnitTest1();
		  //ReassemblyUnitTest2();
		  //ReassemblyUnitTest3();
		  //ReassemblyUnitTest4();
		  //ReassemblyUnitTest5();
		  //ReassemblyUnitTest6();
		  //ReassemblyUnitTest7();
		  //WriteTelemetryTest();
		 // ReadingFromSQLtest();
		  //BlobTests();
	//	  SQLClient.ByteArrayTest();
		//  ReplayContollerTests();
	 // PacketCreationTests();
	  }
	  public static void ReplayContollerTests(){
		  ReplayController temp = new ReplayController();
		  temp.Display();
	  }
	  
	  public static void PacketCreationTests(){
			AX25AddressField src = new AX25AddressField(MissionConstants.satCallsign,MissionConstants.satSSID);
			AX25AddressField dest = new AX25AddressField(MissionConstants.gsCallsign,MissionConstants.gsSSID);
			byte [] data = new byte[4];
			for(int i = 0;i<4;i++){
				data[i] = (byte)i;
			}
			
			AX25Telemetry temp = new AX25Telemetry(dest, src, new AX25FrameIdentification(), (byte)4, (byte)4,(byte)0,  data, new AX25FrameStatus(), 10);
			temp.ProtocolIdentifier = 0x03;
			
			
			byte [] crc = new byte[2];
			crc = CRC16CCITT.generateCRC(temp.ToByteArrayWithoutCRC());
			temp._crc[0] = crc[0];
			temp._crc[1] = crc[1];
			byte [] ggg = temp.ToByteArray();
			AX25Telemetry aaa = new AX25Telemetry(temp.ToByteArray());
			
			for(int i =0;i<aaa.Data.length;i++){
				System.out.println(aaa.Data[i]);
			}
	
			System.out.println(BitOperations.UnsignedBytetoInteger8(aaa.MasterFrameCount));
	  }
	  
	  public static void AX25TelemetryEncodingTest(){
		  byte [] frame = new byte[40];
		  for(int i = 0;i<14;i++){
			  frame[i] = 0x0;
		  }
		  frame[14] = 0x04;
		  frame[15] = (byte)0xF0;
		  frame[16] = 0;
		  frame[17] = 8;
		  frame[18] = 9;
		  frame[19] = 6;
		  for(int i =20;i<40;i++){
			  frame[i] = 1;
		  }
		  AX25Telemetry temp = new AX25Telemetry(frame);
		  System.out.println(temp.FirstHeaderPointer);
		  System.out.println(temp.MasterFrameCount);
		  
	  }
	  // tests a single packet starting in middle
	  public static void ReassemblyUnitTest1(){
		  ReAssemblyUnit temp = new ReAssemblyUnit(2);
		  AX25Telemetry tFrame = new AX25Telemetry();
		  tFrame.FirstHeaderPointer = 0x03;
		  byte[] data = new byte[10];
		  data[7] = 0;
		  data[8] = 0x07;
		  tFrame.SetDataField(data);
		  System.out.println(tFrame.Data.length);
		  temp.ReassemblePacket(tFrame);
		  System.out.println(temp.Completed.get(0).data.length);
		  
	  }
	  
	  // tests to with two packets in a frame with header
	  public static void ReassemblyUnitTest2(){
		  ReAssemblyUnit temp = new ReAssemblyUnit(2);
		  AX25Telemetry tFrame = new AX25Telemetry();
		  tFrame.FirstHeaderPointer = 0x01;
		  byte [] data = new byte[20];
		  data[5] = 0;
		  data[6] = 0x08;
		  data[13] = 0;
		  data[14] = 0x07;
		  tFrame.SetDataField(data);
		  temp.ReassemblePacket(tFrame);
		  System.out.println(temp.Completed.get(1).data.length);
	  }
	  // tests when a beginning of a packet is in this frame with length and rest is in next frame (And it does not have any other firstpoint header
	  public static void ReassemblyUnitTest3(){
		  ReAssemblyUnit temp = new ReAssemblyUnit(2);
		  AX25Telemetry tFrame = new AX25Telemetry();
		  tFrame.VirtualChannelFrameCount = 0;
		  tFrame.FirstHeaderPointer = 0x01;
		  byte [] data = new byte[22];
		  data[5] = 0;
		  data[6] = 0x08;
		  data[13] = 0;
		  data[14] = 0x07;
		  data[20] = 0;
		  data[21] = 0x07;
		  
		  tFrame.SetDataField(data);
		  temp.ReassemblePacket(tFrame);
		  
		  AX25Telemetry tFrame2 = new AX25Telemetry();
		  tFrame2.FirstHeaderPointer = BitOperations.IntegerToUnsignedbyte8(255);
		  byte[] data2 = new byte[10];
		  tFrame2.VirtualChannelFrameCount = 1;
		  tFrame2.SetDataField(data2);
		 
		  temp.ReassemblePacket(tFrame2);
		  System.out.println(temp.Completed.get(2).data.length);
	  }
	  
	  // tests similar to test 3 for reassembly unit, but with packets arriving out or order
	  public static void ReassemblyUnitTest4(){
		  ReAssemblyUnit temp = new ReAssemblyUnit(2);
		  AX25Telemetry tFrame = new AX25Telemetry();
		  tFrame.VirtualChannelFrameCount = 0;
		  tFrame.FirstHeaderPointer = 0x01;
		  byte [] data = new byte[22];
		  data[5] = 0;
		  data[6] = 0x08;
		  data[13] = 0;
		  data[14] = 0x07;
		  data[20] = 0;
		  data[21] = 0x07;
		  
		  tFrame.SetDataField(data);
		
		  
		  AX25Telemetry tFrame2 = new AX25Telemetry();
		  tFrame2.FirstHeaderPointer = BitOperations.IntegerToUnsignedbyte8(255);
		  byte[] data2 = new byte[1];
		  tFrame2.VirtualChannelFrameCount = 1;
		  tFrame2.SetDataField(data2);
		  temp.ReassemblePacket(tFrame2);
		  
		  temp.ReassemblePacket(tFrame);
		  
		  System.out.println(temp.Completed.size());
	  }
	  
	  // tests similar to last one but with a packet spread over three packets
	  public static void ReassemblyUnitTest5(){
		  ReAssemblyUnit temp = new ReAssemblyUnit(2);
		  AX25Telemetry tFrame = new AX25Telemetry();
		  tFrame.VirtualChannelFrameCount = 0;
		  tFrame.FirstHeaderPointer = 0x01;
		  byte [] data = new byte[22];
		  data[5] = 0;
		  data[6] = 0x08;
		  data[13] = 0;
		  data[14] = 0x07;
		  data[20] = 0;
		  data[21] = BitOperations.IntegerToUnsignedbyte8(9);
		  
		  tFrame.SetDataField(data);
		
		  
		  AX25Telemetry tFrame2 = new AX25Telemetry();
		  tFrame2.FirstHeaderPointer = BitOperations.IntegerToUnsignedbyte8(255);
		  byte[] data2 = new byte[1];
		  tFrame2.VirtualChannelFrameCount = 2;
		  tFrame2.SetDataField(data2);
		  
		  
		  AX25Telemetry tFrame3 = new AX25Telemetry();
		  tFrame3.FirstHeaderPointer = BitOperations.IntegerToUnsignedbyte8(255);
		  byte[] data3 = new byte[2];
		  tFrame3.VirtualChannelFrameCount = 1;
		  tFrame3.SetDataField(data3);
		  
		  
		  temp.ReassemblePacket(tFrame2);
		  temp.ReassemblePacket(tFrame3);
		  
		  temp.ReassemblePacket(tFrame);
		  
		  System.out.println(temp.Completed.get(2).data.length);
	  }
	  
	  public static void ReassemblyUnitTest6(){
		  ReAssemblyUnit temp = new ReAssemblyUnit(2);
		  AX25Telemetry tFrame = new AX25Telemetry();
		  tFrame.VirtualChannelFrameCount = 0;
		  tFrame.FirstHeaderPointer = 0x01;
		  byte [] data = new byte[21];
		  data[5] = 0;
		  data[6] = 0x08;
		  data[13] = 0;
		  data[14] = 0x07;
		  data[20] = 0;
		 // data[21] = BitOperations.IntegerToUnsignedbyte8(9);
		  
		  tFrame.SetDataField(data);
		
		  
		  AX25Telemetry tFrame2 = new AX25Telemetry();
		  tFrame2.FirstHeaderPointer = BitOperations.IntegerToUnsignedbyte8(255);
		  byte[] data2 = new byte[1];
		  tFrame2.VirtualChannelFrameCount = 2;
		  tFrame2.SetDataField(data2);
		  
		  
		  AX25Telemetry tFrame3 = new AX25Telemetry();
		  tFrame3.FirstHeaderPointer = BitOperations.IntegerToUnsignedbyte8(255);
		  byte[] data3 = new byte[3];
		  data3[0] = BitOperations.IntegerToUnsignedbyte8(9);
		  tFrame3.VirtualChannelFrameCount = 1;
		  tFrame3.SetDataField(data3);
		  
		  
		  temp.ReassemblePacket(tFrame2);
		  temp.ReassemblePacket(tFrame3);
		  
		  temp.ReassemblePacket(tFrame);
		  
		  System.out.println(temp.Completed.get(2).data.length);
	  }
	  
	  public static void ReassemblyUnitTest7(){
		  ReAssemblyUnit temp = new ReAssemblyUnit(2);
		  AX25Telemetry tFrame = new AX25Telemetry();
		  tFrame.VirtualChannelFrameCount = 0;
		  tFrame.FirstHeaderPointer = 0x01;
		  byte [] data = new byte[22];
		  data[5] = 0;
		  data[6] = 0x08;
		  data[13] = 0;
		  data[14] = 0x07;
		  data[20] = 0;
		  data[21] = BitOperations.IntegerToUnsignedbyte8(9);
		  
		  tFrame.SetDataField(data);
		
		  
		  AX25Telemetry tFrame2 = new AX25Telemetry();
		  tFrame2.FirstHeaderPointer = BitOperations.IntegerToUnsignedbyte8(1);
		  byte[] data2 = new byte[7];
		  data2[5] = 0;
		  data2[6] = 0x06;
		  tFrame2.VirtualChannelFrameCount = 2;
		  tFrame2.SetDataField(data2);
		  
		  
		  AX25Telemetry tFrame3 = new AX25Telemetry();
		  tFrame3.FirstHeaderPointer = BitOperations.IntegerToUnsignedbyte8(255);
		  byte[] data3 = new byte[2];
		  data3[0] = BitOperations.IntegerToUnsignedbyte8(9);
		  tFrame3.VirtualChannelFrameCount = 1;
		  tFrame3.SetDataField(data3);
		  
		  
		  temp.ReassemblePacket(tFrame2);
		  temp.ReassemblePacket(tFrame3);
		  
		  temp.ReassemblePacket(tFrame);
		  
		  System.out.println(temp.Completed.size());
	  }
	  
	  public static void WriteTelemetryTest(){
		  AX25Telemetry tFrame3 = new AX25Telemetry();
		  tFrame3.FirstHeaderPointer = BitOperations.IntegerToUnsignedbyte8(255);
		  byte[] data3 = new byte[10];
		  data3[0] = BitOperations.IntegerToUnsignedbyte8(9);
		  tFrame3.VirtualChannelFrameCount = 4;
		  tFrame3.SetDataField(data3);
		  SQLClient tem = new SQLClient();
		  System.out.println(tFrame3.ToByteArray()[14]);
		 
		  tem.ArchieveAX25TeleMetry(tFrame3);
	  }
	  public static void ReadingFromSQLtest() throws SQLException{
		  SQLClient temp = new SQLClient();
		  temp.retrieveAX25Telemetry("2014-04-26 00:00:00 ", "2014-04-26 10:00:00");
	  }
	  
	  public static void BlobTests() throws SQLException{
		
	  }
	  public static void MakeTelemetryEntries(){
			AX25AddressField src = new AX25AddressField(MissionConstants.satCallsign,MissionConstants.satSSID);
			AX25AddressField dest = new AX25AddressField(MissionConstants.gsCallsign,MissionConstants.gsSSID);
			
			byte [] data = new byte[10];
			data[4] = 0;
			data[5] = BitOperations.IntegerToUnsignedbyte8(10);

			AX25Telemetry temp = new AX25Telemetry(dest, src, new AX25FrameIdentification(), (byte)1, (byte)1,(byte)0,  data, new AX25FrameStatus(), 10);
			
	
		
			
			byte [] crc = new byte[2];
			crc = CRC16CCITT.generateCRC(temp.ToByteArrayWithoutCRC());
			temp._crc[0] = crc[0];
			temp._crc[1] = crc[1];
			
			SQLClient _sql = new SQLClient();
			_sql.ArchieveAX25TeleMetry(temp);
			
	  }

}
