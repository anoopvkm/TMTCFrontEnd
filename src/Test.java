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
import Trace.Trace;
import AX25.AX25Telemetry;
import BitOperations.BitOperations;


public class Test {

	  public static void main(String a[]) throws SQLException, InterruptedException {

		  final LinkedBlockingQueue<String> blah = new LinkedBlockingQueue<String>();
		  
		 
		     JFrame  jfrm=new JFrame("Frame name here");
		     jfrm.setSize(600,600);
		     jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		     JTextArea jta = new JTextArea();
		     jta.setSize(600, 600);
		     
		     jfrm.add(jta);
		     jta.append("fff");
		     jta.append("\n gggg");
		     jfrm.setVisible(true);
		     jfrm.setLayout(new FlowLayout()); 
		     for(int i = 0; i <9 ;i++){
		    	 Thread.sleep(300);
		    	 jta.append("\ngggg");
		     }
		     
		  
		  Thread t = new Thread(new Runnable () {
			  public void run(){
			
				while(!blah.isEmpty()){
					System.out.println("sss");
				}
				
				  System.out.println("lololol");
			  }
		  });
		  t.start();
		  Thread.sleep(5);
		 blah.add("ddd");
		  System.out.println("ethipoi");
		  
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
}
