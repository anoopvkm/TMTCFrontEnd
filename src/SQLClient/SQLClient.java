package SQLClient;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;

import javax.sql.rowset.serial.SerialBlob;

import com.mysql.jdbc.ResultSet;

import TMReceiver.ByteArray;


import AX25.AX25Telecommand;
import AX25.AX25Telemetry;

public class SQLClient {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/TMTCFrontEnd";

	   //  Database credentials
	static final String USER = "root";
	static final String PASS = "ROCKGANA89";
	
	/**
	 * Function to archieve Telemtery packets
	 * Writes to AX
	 * @param arcList
	 * @throws SQLException
	 */
	public void ArchieveTelemetry(LinkedList<AX25Telemetry> arcList) throws SQLException{
		  Connection conn = null;
		  Statement stmt = null;
		  
		  try {
			 
			  Class.forName("com.mysql.jdbc.Driver");
			  
			  conn = DriverManager.getConnection(DB_URL, USER, PASS);
			 
			  stmt = conn.createStatement();
			  
			  System.out.println(arcList.size());
			  for(int i = 0;i<arcList.size();i++){
				  Date date = new Date();
				  Timestamp ts = new Timestamp(date.getTime());
				  Blob b = new SerialBlob(arcList.get(i).ToByteArray());
				//  b.setBytes(1, arcList.get(i).ToByteArray());
				  
				  String query =  "INSERT INTO AX25Telemetry (TimeStamp , Frame ) VALUES ('"+ts +"','"+  b + "') ";
				  stmt.executeUpdate(query);
				
			  }
		     
			  conn.close();
		     
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ArchieveTeleCommand(LinkedList<AX25Telecommand> arcList) throws SQLException{
		  Connection conn = null;
		  Statement stmt = null;
		  
		  try {
			 
			  Class.forName("com.mysql.jdbc.Driver");
			  
			  conn = DriverManager.getConnection(DB_URL, USER, PASS);
			 
			  stmt = conn.createStatement();
			  
			  System.out.println(arcList.size());
			  for(int i = 0;i<arcList.size();i++){
				  Date date = new Date();
				  Timestamp ts = new Timestamp(date.getTime());
				  Blob b = new SerialBlob(arcList.get(i).ToByteArray());
				//b.setBytes(1, arcList.get(i).ToByteArray());
				
				  
				  String query =  "INSERT INTO AX25Telecommand (TimeStamp , Frame ) VALUES ('"+ts +"','"+  b + "') ";
				  stmt.executeUpdate(query);
				
			  }
		     
			  conn.close();
		     
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to write a Single AX25 TelecommanPacket
	 * @param AX25TeleCommand
	 */
	public void ArchieveAX25TeleCommand(final AX25Telecommand Frame){
		Thread writeToSQLThread = new Thread (new Runnable(){
			public void run(){
				LinkedList<AX25Telecommand> ListToWrite = new LinkedList<AX25Telecommand>();
				ListToWrite.add(Frame);
				try {
					ArchieveTeleCommand(ListToWrite);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		writeToSQLThread.start();
	}
	
	/**
	 * Function to write a Single AX25 TelecommanPacket
	 * @param AX25TeleCommand
	 */
	public void ArchieveAX25TeleMetry(final AX25Telemetry Frame){
		Thread writeToSQLThread = new Thread (new Runnable(){
			public void run(){
				LinkedList<AX25Telemetry> ListToWrite = new LinkedList<AX25Telemetry>();
				ListToWrite.add(Frame);
				try {
					ArchieveTelemetry(ListToWrite);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		writeToSQLThread.start();
	}
	
	/**
	 * Function to return list of arrays which comes in a time range
	 * @param start , the start timestamp
	 * @param end, the end timestamp
	 * @return List of bytearrays 
	 * @throws SQLException 
	 */
	public LinkedList<ByteArray> retrieveAX25Telemetry(String start, String end) throws SQLException{
		LinkedList<ByteArray> list = new LinkedList<ByteArray>();
		 Connection conn = null;
		  Statement stmt = null;
		  
		  try {
			 
			  Class.forName("com.mysql.jdbc.Driver");
			  
			  conn = DriverManager.getConnection(DB_URL, USER, PASS);
			 
			  stmt = conn.createStatement();
			  
			  String query =  "SELECT * FROM AX25Telemetry WHERE Counter = 2012";
			  java.sql.ResultSet rs = stmt.executeQuery(query);
		     
			  while (rs.next()){
				  System.out.println("fff");
				//(assuming you have a ResultSet named RS)
				  Blob blob = rs.getBlob("TimeStamp");

				  int blobLength = (int) blob.length();  
				  byte[] blobAsBytes = blob.getBytes(1, blobLength);
				  System.out.println(blobAsBytes.length);
				  //release the blob and free up memory. (since JDBC 4.0)
			
			  }
			  conn.close();
		     
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return list;
	}
}
