package Trace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * This class implements logger functionality
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class Trace {

	// counter to keep track of number of lines in the log flag
	private static int linecount = 0;
	
	// writer handle
	private static PrintWriter writer = null;
	
	// the path of the log file
	private static String path = null;
	
	// opens the trace file
	public static void SetTraceFile(String _path) throws IOException{
		writer = new PrintWriter(_path, "UTF-8");
		path = _path;
	}
	
	// closes the trace file
	public static void CloseTrace(){
		writer.close();
	}
	
	// calender instance to get time
	private static Calendar cal = Calendar.getInstance();
	
	// date instance
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
	
	/* Function to write a line to trace file
	 * @param line , line to be written to file
	 */
	public static void WriteLine(String line) throws FileNotFoundException, UnsupportedEncodingException{
		
		cal.getTime();
    	
    	
    	
		if(linecount < 1000){
			linecount++;
			writer.println(sdf.format(cal.getTime())+" "+line);
			writer.flush();
		}
		else{
			linecount = 0;
			writer.close();
			writer = new PrintWriter(path,"UTF-8");
			writer.println(sdf.format(cal.getTime())+" "+line);
			writer.flush();
		}
		
	}
}
