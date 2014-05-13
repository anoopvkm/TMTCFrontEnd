package SQLClient;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;

import javax.sql.rowset.serial.SerialBlob;

import com.mysql.jdbc.Blob;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

import TMTCFrontEnd.ByteArray;


import AX25.AX25Telecommand;
import AX25.AX25Telemetry;

/**
 * This class handles all SQL operations
 * @author anoop
 *
 */
public class SQLClient {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/TMTCFrontEnd";

	   //  Database credentials
	static final String USER = "root";
	static final String PASS = "ROCKGANA89";
	
	/**
	 * Function to archieve Telemtery packets
	 * Writes to AX25Telemetry table
	 * @param arcList list of frames to be written to database
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
				  byte [] frame = arcList.get(i).ToByteArray();
				  
				  
				  String query =  "INSERT INTO AX25Telemetry (TimeStamp , Frame ) VALUES ('"+ts +"', ? ) ";
				  
				  
				  java.sql.PreparedStatement pp = conn.prepareStatement(query);
				  
				  pp.setBytes(1, frame);
				  pp.executeUpdate();
				
			  }
		     
			  conn.close();
		     
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to write telecommands ton AX25Telecommand table in database
	 * @param arcList list of telecommands to be written
	 * @throws SQLException
	 */
	public void ArchieveTeleCommand(LinkedList<AX25Telecommand> arcList) throws SQLException{
		  Connection conn = null;
		  Statement stmt = null;
		  
		  try {
			 
			  Class.forName("com.mysql.jdbc.Driver");
			  
			  conn = DriverManager.getConnection(DB_URL, USER, PASS);
			 
			  stmt = conn.createStatement();
			  
		
			  for(int i = 0;i<arcList.size();i++){
				  Date date = new Date();
				  Timestamp ts = new Timestamp(date.getTime());
				  
				  byte [] frame = arcList.get(i).ToByteArray();
				  
				  
				  String query =  "INSERT INTO AX25Telecommand (TimeStamp , Frame ) VALUES ('"+ts +"', ? ) ";
				  
				  java.sql.PreparedStatement pp = conn.prepareStatement(query);
				  
				  pp.setBytes(1, frame);
				  pp.executeUpdate();
				  
				
			  }
		     
			  conn.close();
		     
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to write a Single AX25 TelecommanPacket
	 * @param AX25TeleCommand frame to be written to database
	 */
	public void ArchieveAX25TeleCommand(final AX25Telecommand Frame){
		LinkedList<AX25Telecommand> ListToWrite = new LinkedList<AX25Telecommand>();
		ListToWrite.add(Frame);
		try {
			ArchieveTeleCommand(ListToWrite);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to write a Single AX25 TelecommanPacket
	 * @param AX25TeleCommand frame to be written to database
	 */
	public void ArchieveAX25TeleMetry(final AX25Telemetry Frame){

		LinkedList<AX25Telemetry> ListToWrite = new LinkedList<AX25Telemetry>();
		ListToWrite.add(Frame);
		try {
			ArchieveTelemetry(ListToWrite);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to return list of arrays which comes in a time range
	 * @param start , the start timestamp
	 * @param end, the end timestamp
	 * @return LinkedList of bytearrays 
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
			  
			  String query =  "SELECT * FROM AX25Telemetry WHERE TimeStamp >  ?  AND TimeStamp < ? ";
			  java.sql.PreparedStatement ps = conn.prepareStatement(query);
			  ps.setString(1, start);
			  ps.setString(2, end);
			  java.sql.ResultSet rs = ps.executeQuery();
		     
			  while (rs.next()){
				  ByteArray tRes = new ByteArray();
				  tRes.data = rs.getBytes("Frame");
				  list.add(tRes);
			
			
			  }
			  conn.close();
		     
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return list;
	}
	
	// test function
	public static void ByteArrayTest() throws SQLException{
		 Connection conn = null;
		  Statement stmt = null;
		  
		  try {
			 
			  Class.forName("com.mysql.jdbc.Driver");
			  
			  conn = DriverManager.getConnection(DB_URL, USER, PASS);
			 byte [] ddd = new byte[30];
			 ddd[10] = 4;
			  stmt = conn.createStatement();
			  String query =  "INSERT INTO CUSTOM (ID , NAME ) VALUES (8 , ? ) ";
			  java.sql.PreparedStatement pp = conn.prepareStatement(query);
			  
			  pp.setBytes(1, ddd);
			  pp.executeUpdate();
			  
			  query = "SELECT * FROM CUSTOM WHERE ID = 8";
			  pp = conn.prepareStatement(query);
			  java.sql.ResultSet rs = pp.executeQuery();
			  while(rs.next()){
				  System.out.println(rs.getBytes("NAME")[10]);
			  }
			  
			 
			
			  conn.close();
		     
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
