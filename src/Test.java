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

import SQLClient.SQLClient;
import TMReceiver.ReAssemblyUnit;
import TMTCFrontEnd.FrontEnd;
import Trace.Trace;
import AX25.AX25Telemetry;
import BitOperations.BitOperations;


public class Test {

	  public static void main(String a[]) throws SQLException, InterruptedException, IOException {
		  Trace.SetTraceFile("/home/anoop/blah");
		  //FrontEnd test = new FrontEnd();
		  
		  //test.Simulator();
		  //test.Start();
		  //ReassemblyUnitTest1();
		  //ReassemblyUnitTest2();
		  //ReassemblyUnitTest3();
		  //ReassemblyUnitTest4();
		  //ReassemblyUnitTest5();
		  //ReassemblyUnitTest6();
		  //ReassemblyUnitTest7();
		 // WriteTelemetryTest();
		  ReadingFromSQLtest();
		  //BlobTests();
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
		  byte[] data3 = new byte[2];
		  data3[0] = BitOperations.IntegerToUnsignedbyte8(9);
		  tFrame3.VirtualChannelFrameCount = 1;
		  tFrame3.SetDataField(data3);
		  SQLClient tem = new SQLClient();
		  tem.ArchieveAX25TeleMetry(tFrame3);
	  }
	  public static void ReadingFromSQLtest() throws SQLException{
		  SQLClient temp = new SQLClient();
		  temp.retrieveAX25Telemetry("", "");
	  }
	  
	  public static void BlobTests(){
		  
	  }

}
