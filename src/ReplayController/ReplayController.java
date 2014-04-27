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
import TMReceiver.ByteArray;
import TMReceiver.ReAssemblyUnit;
import TMTCFrontEnd.MissionConstants;

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
	
	JPanel vcidPanel;
	JPanel timePanel;
	JPanel msgPanel;
	
	JLabel vcidLabel;
	JLabel tStart;
	JLabel tEnd;
	JLabel time;
	
	JTextArea msg;
	
	JTextField vcidval;
	JTextField sVal;
	JTextField tVal;
	
	JButton startButton ; 
	
	// to read from sql
	SQLClient _sqlClient ; 
	

	
	// constructor
	public ReplayController(){
		
		_sqlClient = new SQLClient();
		
		controllerFrame = new JFrame();
		
		controllerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        controllerFrame.setTitle("ReplayController");
        controllerFrame.setSize(350,300);
        
        vcidPanel = new JPanel();
        timePanel = new JPanel();
        msgPanel = new JPanel();
        
        vcidLabel = new JLabel("VCID (0 - 7) : ");
        tStart = new JLabel("Start : ");
        tEnd = new JLabel("End : ");
        time = new JLabel("Time Range (YYYY-MM-DD HH:MM:SS ) ");
        
        msg = new JTextArea();
        
        vcidval = new JTextField();
        sVal = new JTextField();
        tVal = new JTextField();
        
        startButton = new JButton("Replay");
        startButton.addActionListener(new ActionListener() {
        	 
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Blah");
                String sVcid = vcidval.getText();
                String start = sVal.getText();
                String end = tVal.getText();
                int iVcid = Integer.valueOf(sVcid);
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
                }
            }
        });      
 
        
        vcidval.setColumns(3);
        sVal.setColumns(15);
        tVal.setColumns(15);
        msg.setColumns(25);
        msg.setRows(4);
        msg.setEditable(false);
        vcidPanel.add(vcidLabel);
        vcidPanel.add(vcidval);
        
        timePanel.add(time);
        timePanel.add(tStart);
        timePanel.add(sVal);
        timePanel.add(tEnd);
        timePanel.add(tVal);
        timePanel.add(startButton);
        
        
        controllerFrame.add(vcidPanel,BorderLayout.NORTH);
        controllerFrame.add(timePanel,BorderLayout.CENTER);
        controllerFrame.add(msg,BorderLayout.SOUTH);
        
	}
	
	/**
	 * Function to display the gui
	 */
	public void Display(){
		controllerFrame.setVisible(true);
	}
	
	/**
	 * Function which processes the request
	 * @param vcid : virtual channel id
	 * @param start: the start time stamp 
	 * @param end : the end time stamp 
	 * @throws SQLException 
	 */
	private void processRequest(int vcid, String start,String end) throws SQLException{
		
		
		LinkedList<ByteArray> retrieved =  new LinkedList<ByteArray>();
		retrieved = _sqlClient.retrieveAX25Telemetry(start,end);
		
		LinkedList<AX25Telemetry> retrievedDecoded = new LinkedList<AX25Telemetry>();
		for(int i =0; i<retrieved.size();i++){
			AX25Telemetry temp = new AX25Telemetry(retrieved.get(i).data);
			int tvcid = BitOperations.UnsignedBytetoInteger8(temp.FrameIdentification.VirtualChannelID);
			if(tvcid == vcid){
				retrievedDecoded.add(temp);
			}
			
		}
		msg.setText("Number of packets : "+ retrievedDecoded.size());
		ReAssemblyUnit _reassemble = new ReAssemblyUnit(vcid);
		for(int i =0 ;i<retrievedDecoded.size();i++){
			_reassemble.ReassemblePacket(retrievedDecoded.get(i));
		}
		
	}
	
	
}
