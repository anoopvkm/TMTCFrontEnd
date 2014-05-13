package ReplayController;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import AX25.AX25Telemetry;
import BitOperations.BitOperations;
import SQLClient.SQLClient;
import TMReceiver.ReAssemblyUnit;
import TMTCFrontEnd.ByteArray;
import TMTCFrontEnd.MissionConstants;
import Trace.Trace;

import swing2swt.layout.BorderLayout;
import swing2swt.layout.FlowLayout;

/**
 * This class deals with ReplayController
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class ReplayController {

	// The frame object
	JFrame controllerFrame ;
	
	
	// the main panel
	JPanel MainPanel;
	
	// different labels 
	JLabel vcidLabel;
	JLabel tStart;
	JLabel tEnd;
	JLabel time;
	
	JTextArea msg;
	
	JTextField vcidval;
	JTextField startTimeVal;
	JTextField endTimeVal;
	
	JButton startButton ; 
	
	// to read from sql
	SQLClient _sqlClient ; 
	

	
	// constructor
	public ReplayController(){
		
		// creatign sql objects
		_sqlClient = new SQLClient();
		
		
		// creating all GUI objects
		controllerFrame = new JFrame();
		
		controllerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        controllerFrame.setTitle("ReplayController");
        controllerFrame.setSize(350,300);
        
       
        MainPanel = new JPanel();
        MainPanel.setSize(350, 300);
        MainPanel.setLayout(null);
        
        vcidLabel = new JLabel("VCID (0 - 7) : ");
        tStart = new JLabel("Start : ");
        tEnd = new JLabel("End : ");
        time = new JLabel("Time Range (YYYY-MM-DD HH:MM:SS ) ");
        
        vcidLabel.setBounds(50, 20, 100, 50);
        time.setBounds(40, 70, 300, 50);
        tStart.setBounds(40, 90, 50, 50);
        tEnd.setBounds(40, 110, 50, 50);
        
        msg = new JTextArea();
        
        vcidval = new JTextField();
        startTimeVal = new JTextField();
        endTimeVal = new JTextField();
        
        startButton = new JButton("Replay");
        
        // replay button action listener
        startButton.addActionListener(new ActionListener() {
        	 
       
            public void actionPerformed(ActionEvent e)
            {
                
                String sVcid = vcidval.getText();
                String start = startTimeVal.getText();
                String end = endTimeVal.getText();
                int iVcid = Integer.valueOf(sVcid);
                Trace.WriteLine("[REPLAY] Replay button pressed");
                if(iVcid >= 0 && iVcid <=7){
                	msg.setText("");
                	try {
						processRequest(iVcid,start,end);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }else{
                	msg.setText("Wrong value of VCID" );
                	Trace.WriteLine("[REPLAY] Wrong value of VCID");
                }
            }
        });      
 
        
        // adding all GUI components to the panel
        
        vcidval.setBounds(150,38,30,20);
        startTimeVal.setBounds(100,108,200,20);
        endTimeVal.setBounds(100,130,200,20);
        startButton.setBounds(150, 160, 90, 30);
        msg.setBounds(30, 200, 300, 60);
        msg.setEditable(false);
        
        MainPanel.add(vcidLabel);
        MainPanel.add(vcidval);
        MainPanel.add(time);
        MainPanel.add(tStart);
        MainPanel.add(tEnd);
        MainPanel.add(startTimeVal);
        MainPanel.add(endTimeVal);
        MainPanel.add(startButton);
        MainPanel.add(msg);
        controllerFrame.add(MainPanel);
   
        
	}
	
	/**
	 * Function to display the gui
	 */
	public void Display(){
		controllerFrame.setVisible(true);
	}
	
	/**
	 * Function which processes the request
	 * @param vcid  virtual channel id
	 * @param start the start time stamp 
	 * @param end  the end time stamp 
	 * @throws SQLException 
	 */
	private void processRequest(int vcid, String start,String end) throws SQLException{
		
		// list to store frames retrieved from database
		LinkedList<ByteArray> retrieved =  new LinkedList<ByteArray>(); 
		
		// SQL call to obtain list of frames
		retrieved = _sqlClient.retrieveAX25Telemetry(start,end);

		LinkedList<AX25Telemetry> retrievedDecoded = new LinkedList<AX25Telemetry>();
		
		// filtering out frames fromt he particular virtual channel
		for(int i =0; i<retrieved.size();i++){
			AX25Telemetry temp = new AX25Telemetry(retrieved.get(i).data);
			int tvcid = BitOperations.UnsignedBytetoInteger8(temp.FrameIdentification.VirtualChannelID);
			if(tvcid == vcid){
				retrievedDecoded.add(temp);
			}
			
		}
		msg.setText("Number of Frames : "+ retrievedDecoded.size());
		Trace.WriteLine("[REPLAY] Number of frames retrieved : "+retrievedDecoded.size());
		
		// creating a reassembly unit
		ReAssemblyUnit _reassemble = new ReAssemblyUnit(vcid);
		
		// passing frames to the reassembly unit
		for(int i =0 ;i<retrievedDecoded.size();i++){
			_reassemble.ReassemblePacket(retrievedDecoded.get(i));
		}
		msg.append("\n Number of reassembled packets : "+_reassemble.Completed.size() );
	
	}
	
	
}
